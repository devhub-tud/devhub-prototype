package nl.tudelft.ewi.dea;

import javax.annotation.concurrent.Immutable;

import lombok.experimental.Value;

@Value
@Immutable
public final class BuildInfo {
	private final String date = null;
	private final String version = null;
}
