package top.starrysea.hateoas;

import java.util.Map;

import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

public class StarryseaMediaType extends MediaType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4960748344746789573L;

	public StarryseaMediaType(String type, String subtype, @Nullable Map<String, String> parameters) {
		super(type, subtype, parameters);
	}

	public static final StarryseaMediaType APPLICATION_JSON_STARRYSEA;
	private static final String APPLICATION_JSON_STARRYSEA_VALUE = "application/starrysea+json";
	static {
		APPLICATION_JSON_STARRYSEA = valueOf(APPLICATION_JSON_STARRYSEA_VALUE);
	}

	public static StarryseaMediaType valueOf(String value) {
		return parseMediaType(value);
	}

	public static StarryseaMediaType parseMediaType(String mediaType) {
		MimeType type;
		try {
			type = MimeTypeUtils.parseMimeType(mediaType);
		} catch (InvalidMimeTypeException ex) {
			throw ex;
		}
		try {
			return new StarryseaMediaType(type.getType(), type.getSubtype(), type.getParameters());
		} catch (IllegalArgumentException ex) {
			throw new InvalidMediaTypeException(mediaType, ex.getMessage());
		}
	}
}
