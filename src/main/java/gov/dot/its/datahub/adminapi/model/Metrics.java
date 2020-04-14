package gov.dot.its.datahub.adminapi.model;

public class Metrics {
	private Long downloadsTotal;
	private Long pageViewsLastMonth;
	private Long pageViewsTotal;

	public Long getDownloadsTotal() {
		return downloadsTotal;
	}
	public void setDownloadsTotal(Long downloadsTotal) {
		this.downloadsTotal = downloadsTotal;
	}
	public Long getPageViewsLastMonth() {
		return pageViewsLastMonth;
	}
	public void setPageViewsLastMonth(Long pageViewsLastMonth) {
		this.pageViewsLastMonth = pageViewsLastMonth;
	}
	public Long getPageViewsTotal() {
		return pageViewsTotal;
	}
	public void setPageViewsTotal(Long pageViewsTotal) {
		this.pageViewsTotal = pageViewsTotal;
	}

}
