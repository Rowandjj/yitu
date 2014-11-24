package cn.edu.chd.domain;

public class ImageBean
{
	/**
	 * 文件夹名
	 */
	private String folderName;
	/**
	 * 文件夹中第一张图片名
	 */
	private String topImagePath;
	
	public String getFolderName()
	{
		return folderName;
	}
	public void setFolderName(String folderName)
	{
		this.folderName = folderName;
	}
	public String getTopImagePath()
	{
		return topImagePath;
	}
	public void setTopImagePath(String topImagePath)
	{
		this.topImagePath = topImagePath;
	}
	
}
