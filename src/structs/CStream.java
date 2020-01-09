package structs;

import structs.CChunk;

import java.util.ArrayList;

public class CStream{
	public CStream(String type,
			String name,
			String subtype,
			long chunks,
			long timescale,
			String url,
			int qlevels) {
		this.type = type;
		this.name = name;
		this.subtype = subtype;
		this.chunks = chunks;
		this.timescale = timescale;
		this.url = url;
		this.qlevels = qlevels;
	}
	String type;
	String name;
	String subtype;
	long chunks;
	long timescale;
	String url;
	int qlevels;
	public void SetTimescale(long timescale) {
		this.timescale = timescale;
	}
	public long GetTimescale() {
		return this.timescale;
	}
	public ArrayList<CChunk> qChunk = new ArrayList<CChunk>();
	public ArrayList<Double> qCTime = new ArrayList<Double>();
	public String getType() {
		return type;
	}
	public String getName() {
		return name;
	}
}
