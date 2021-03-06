package de.otaris.zertapps.privacychecker.database.interfaces;


/**
 * represents all similarities between AppCompact and AppExtended
 *
 */
public interface App {

	public int getId();

	public int getCategoryId();

	public String getVersion();

	public String getName();

	public String getLabel();

	public boolean isInstalled();

	public Long getTimestamp();

	public float getPrivacyRating();

	public float getFunctionalRating();

	public String getDescription();

	public float getAutomaticRating();

	public void setAutomaticRating(float automaticRating);

	public byte[] getIcon();
	
	public int getFunctionalRatingCounter();
	
	public String getDeveloper();

	public void setCategoryId(int categoryId);

	public void setLabel(String label);

	public void setVersion(String version);

	public void setName(String name);

	public void setInstalled(boolean installed);

	public void setPrivacyRating(float privacyRating);

	public void setFunctionalRating(float functionalRating);

	public void setDescription(String description);

	public void setIcon(byte[] icon);
	
	public void setFunctionalRatingCounter(int functionalRatingCounter);
	
	public void setDeveloper(String developer);

}
