package nl.tudelft.ewi.dea.jaxrs.api.course;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import nl.tudelft.ewi.dea.DevHubException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.inject.Singleton;

@Singleton
@ThreadSafe
class RepositoryDownloader {

	private static final Logger LOG = LoggerFactory.getLogger(RepositoryDownloader.class);

	private static class DeteFileListener implements RemovalListener<String, File> {

		@Override
		public void onRemoval(RemovalNotification<String, File> notification) {
			LOG.debug("Deleting cached zip file {} ", notification.getValue().getAbsolutePath());
			try {
				FileUtils.deleteDirectory(notification.getValue());
			} catch (IOException e) {
				LOG.warn("Failed to delete tmp directory: " + e.getLocalizedMessage(), e);
			}
		}
	}

	private final Cache<String, File> cache;

	RepositoryDownloader() {
		cache = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.HOURS).removalListener(new DeteFileListener()).build();
	}

	/**
	 * @param repositories The repositories you want in the zipfile.
	 * @return The hashcode of the file. This can be user to download the file
	 *         useing {@link #getFile(String)}.
	 * @throws DevHubException if anything goes wrong.
	 */
	String prepareDownload(Set<String> repositories) throws DevHubException {
		LOG.trace("Preparing to download repos {}", repositories);
		File zippedFile;
		ZipOutputStream zipOut;
		try {
			zippedFile = File.createTempFile("zipped-repos", ".zip");
			zipOut = new ZipOutputStream(new FileOutputStream(zippedFile));
			for (String repoUrl : repositories) {
				cloneRepoInFolder(repoUrl, zipOut);
			}
			zipOut.close();
		} catch (IOException e) {
			throw new DevHubException("Error while creating zipfile " + e.getLocalizedMessage(), e);
		}
		String md5 = generateMd5(zippedFile);
		cache.invalidate(md5);
		cache.put(md5, zippedFile);
		LOG.debug("Created zipped repos in folder {} with MD5 hash {}", zippedFile.getAbsolutePath(), md5);
		return md5;
	}

	private void cloneRepoInFolder(String repoUrl, ZipOutputStream zipOut) throws IOException {
		String reponame = repoUrl.substring(repoUrl.lastIndexOf('/') + 1);
		File tmpFolder = Files.createTempDir();
		LOG.debug("Cloning repo with name {} from url {} into folder {}", reponame, repoUrl, tmpFolder.getAbsolutePath());
		Git.cloneRepository().setURI(repoUrl).setDirectory(tmpFolder).call();
		LOG.trace("Cloning complete. Writing to zip");
		writeFolderToZip(tmpFolder, zipOut, tmpFolder.getAbsolutePath(), reponame);
		LOG.trace("Removing tmp dir");
		FileUtils.deleteDirectory(tmpFolder);
		LOG.trace("Repo added to zip");
	}

	private void writeFolderToZip(File folder, ZipOutputStream zip, String baseName, String repoName)
			throws IOException {
		File[] files = folder.listFiles();
		CRC32 crc = new CRC32();
		for (File file : files) {
			if (file.isDirectory()) {
				writeFolderToZip(file, zip, baseName, repoName);
			} else {
				String name = repoName + file.getAbsolutePath().substring(baseName.length());
				LOG.debug("Name {} derived vrom {}", name, baseName);
				ZipEntry entry = new ZipEntry(name);
				zip.putNextEntry(entry);
				InputStream in = new FileInputStream(file);
				ByteStreams.copy(in, zip);
				in.close();
				entry.setCrc(calculateCrc(crc, file));
				zip.closeEntry();
			}
		}
	}

	private long calculateCrc(CRC32 crc, File file) throws FileNotFoundException, IOException {
		crc.reset();
		InputStream in = new FileInputStream(file);
		crc.update(ByteStreams.toByteArray(in));
		in.close();
		return crc.getValue();
	}

	private String generateMd5(File tmpFolder) {
		try (FileInputStream in = new FileInputStream(tmpFolder)) {
			HashCode hash = Hashing.md5().hashBytes(ByteStreams.toByteArray(in));
			return hash.toString();
		} catch (IOException e) {
			throw new DevHubException("Could not generate hash for file " + tmpFolder.getAbsolutePath());
		}
	}

	File getFile(@Nullable String hash) {
		return cache.getIfPresent(hash);
	}
}
