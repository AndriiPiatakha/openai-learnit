package com.itbulls.learnit.openai.entities;

import java.util.Arrays;

public class WeatherParameterProperties implements ParameterProperties {
	
	private Location location;
	private MeasurementUnit unit;
	
	public class Location {
		private String type;
		private String description;
		public Location() {
		}
		public Location(String type, String description) {
			this.type = type;
			this.description = description;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		@Override
		public String toString() {
			return "Location [type=" + type + ", description=" + description + "]";
		}
	}
	
	public class MeasurementUnit {
		public static final String CELSIUS = "celsius";
		public static final String FAHRENHEIT = "fahrenheit";
		private String type;
		private String description;
		private String[] enumValues;
		public MeasurementUnit() {
		}
		public MeasurementUnit(String type, String description, String[] enumValues) {
			this.type = type;
			this.description = description;
			this.enumValues = enumValues;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String[] getEnumValues() {
			return enumValues;
		}
		public void setEnumValues(String[] enumValues) {
			this.enumValues = enumValues;
		}
		
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		@Override
		public String toString() {
			return "MeasurementUnit [type=" + type + ", description=" + description + ", enumValues="
					+ Arrays.toString(enumValues) + "]";
		}
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public MeasurementUnit getUnit() {
		return unit;
	}

	public void setUnit(MeasurementUnit unit) {
		this.unit = unit;
	}

	@Override
	public String toString() {
		return "WeatherParameterProperties [location=" + location + ", unit=" + unit + "]";
	}
}
