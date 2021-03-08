package br.com.monkey.ecx.core.model;

import javax.xml.bind.annotation.XmlEnumValue;

public enum MessageType {

	/**
	 * This enums are a Type of message error and the users see this, is more beauty use
	 * camel case them uppercase.
	 */
	@XmlEnumValue("Invalid_Token")
	Invalid_Token("Invalid Token"),

	@XmlEnumValue("Parameter_Error")
	Mult_Status_Error("The request is finished with errors."),

	@XmlEnumValue("Parameter_Error")
	No_Content_Error("The identifier that already exists."),

	@XmlEnumValue("Parameter_Error")
	Conflict_Error("The identifier that already exists."),

	@XmlEnumValue("Parameter_Error")
	Parameter_Error("A require param was missing, or malformed."),

	@XmlEnumValue("Bad_Request_Error")
	Bad_Request_Error("Request invalid or malformed."),

	@XmlEnumValue("Business_Logic_Error")
	Business_Logic_Error("Business logic error."),

	@XmlEnumValue("Resource_Not_Found")
	Resource_Not_Found("Resource not found."),

	@XmlEnumValue("Internal_Architecture_Error")
	Internal_Architecture_Error("Ooops! some big problem found."),

	@XmlEnumValue("List_Not_Found")
	List_Not_Found("List not found."),

	@XmlEnumValue("Method_Not_Allowed")
	Method_Not_Allowed("Method Not Allowed"),

	@XmlEnumValue("Unsupported_Media_Type")
	Unsupported_Media_Type("The request entity is in a format not supported."),

	@XmlEnumValue("Access_Denied")
	Access_Denied("Access denied."),

	@XmlEnumValue("Integration_Error")
	Integration_Error("Error on services contract.");

	private final String description;

	MessageType(final String des) {
		description = des;
	}

	public String getDescription() {
		return description;
	}

}
