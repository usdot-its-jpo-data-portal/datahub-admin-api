package gov.dot.its.datahub.adminapi.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import gov.dot.its.datahub.adminapi.model.ApiError;


@Component
public class ApiUtils {

	private static final Logger loggerapi = LoggerFactory.getLogger(ApiUtils.class);

	@Value("${datahub.admin.api.debug}")
	private boolean debug;

	private static final String MESSAGE_TEMPLATE = "%s : %s ";
	private static final String ERROR_LABEL = "Error";

	public String getQueryParamString(Map<String, String> params,String name, String defaultValue) {
		for(Map.Entry<String, String> entry : params.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(name)) {
				return entry.getValue();
			}
		}
		return defaultValue;
	}

	public int getQueryParamInteger(Map<String, String> params, String name, int defaultValue) {
		String val = this.getQueryParamString(params, name, null);
		if (val == null) {
			return defaultValue;
		}

		int result = -1;
		try {
			result = Integer.parseInt(val);
		} catch(NumberFormatException e) {
			result = defaultValue;
		}

		return result;
	}

	public String getMd5(String source) {
		if (source == null) {
			return null;
		}

		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5"); //NOSONAR
			messageDigest.update(source.trim().getBytes());
			byte[] bytes = messageDigest.digest();
			return DatatypeConverter.printHexBinary(bytes).toLowerCase();
		} catch (NoSuchAlgorithmException e) {
			loggerapi.error(e.getMessage());
			return null;
		} 
	}

	public Date getCurrentUtc() {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		String timstampStr = df.format(date);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Date parsedDate;
		try {
			parsedDate = dateFormat.parse(timstampStr);
			return parsedDate;
		} catch (ParseException e) {
			return date;
		}
	}

	public Timestamp getCurrentUtcTimestamp() {
		Date date = this.getCurrentUtc();
		return Timestamp.valueOf(getTimestampFormat(date, "yyyy-MM-dd HH:mm:ss"));
	}

	public String getTimestampFormat(Date date, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(date);
	}

	public List<ApiError> getErrorsFromException(List<ApiError> errors, Exception e) {
		errors.add(new ApiError(String.format(MESSAGE_TEMPLATE, ERROR_LABEL, e.getMessage())));
		loggerapi.error(String.format(MESSAGE_TEMPLATE, ERROR_LABEL, e.getMessage()));
		if (debug) {
			loggerapi.error(String.format(MESSAGE_TEMPLATE, ERROR_LABEL, e.toString()));
		}
		if (e.getSuppressed().length > 0) {
			for (Throwable x : e.getSuppressed()) {
				errors.add(new ApiError(String.format(MESSAGE_TEMPLATE, ERROR_LABEL, x.toString())));
				loggerapi.error(String.format(MESSAGE_TEMPLATE, ERROR_LABEL, x.toString()));
			}
		}
		return errors;
	}

	public String getUUID() {
		return UUID.randomUUID().toString();
	}

	public String stringFormat(String template, Object... values) {
		int n = StringUtils.countOccurrencesOf(template, "%s");
		if (n == values.length) {
			return String.format(template, values);
		}
		Object[] result = new Object[n];
		Arrays.fill(result, "");
		if (n > values.length) {
			System.arraycopy(values, 0, result, 0, values.length);
			return String.format(template, result);
		}

		System.arraycopy(values, 0, result, 0, result.length);
		StringBuilder s = new StringBuilder();
		for(int i=n; i<values.length; i++) {
			s.append(values[i] == null ? "": values[i] + " ");
		}
		result[result.length-1] = (result[result.length-1] == null ? "" : result[result.length-1])+ " " +s.toString();
		return String.format(template, result);
	}
}
