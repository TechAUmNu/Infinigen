package main.java.com.ionsystems.infinigen.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSearch {

	private String fileNameToSearch;
	private List<File> result = new ArrayList<File>();

	public String getFileNameToSearch() {
		return fileNameToSearch;
	}

	public void setFileNameToSearch(String fileNameToSearch) {
		this.fileNameToSearch = fileNameToSearch;
	}

	public List<File> getResult() {
		return result;
	}

	public static List<File> searchForFile(String directory, String filename) {
		FileSearch fileSearch = new FileSearch();
		fileSearch.searchDirectory(new File(directory), filename);
		return fileSearch.getResult();
	}

	public void searchDirectory(File directory, String fileNameToSearch) {

		setFileNameToSearch(fileNameToSearch);

		if (directory.isDirectory()) {
			search(directory);
		} else {
			System.out.println(directory.getAbsoluteFile().toString() + " is not a directory!");
		}

	}

	private void search(File file) {
		if (file.isDirectory()) {
			if (file.canRead()) {
				for (File temp : file.listFiles()) {
					if (temp.isDirectory()) {
						search(temp);
					} else {
						if (getFileNameToSearch().equals(temp.getName().toLowerCase())) {
							result.add(temp.getAbsoluteFile());
						}
					}
				}

			}

		}
	}

}