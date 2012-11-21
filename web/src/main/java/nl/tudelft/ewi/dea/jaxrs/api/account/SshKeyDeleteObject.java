package nl.tudelft.ewi.dea.jaxrs.api.account;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SshKeyDeleteObject {

	private final List<Long> keyIds = null;

}
