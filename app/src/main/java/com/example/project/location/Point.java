package com.example.project.location;

public class Point{

	public final double latitude;
	public final double longitude;
	public Point(double lat, double lon) {
		latitude = lat;
		longitude = lon;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
}