import data.Name;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class NameLoader {

	/**
	 * Loads a list of names from a specified filepath.
	 * The names have to be separated by a single space.
	 * If an error occurs, an empty list is returned.
	 * @param filePath of file to read from
	 * @return list of names
	 */
	public static List<Name> loadNames(String filePath) {
		List<Name> nameList = new ArrayList<>(1000);

		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
			String line;

			while ((line = bufferedReader.readLine()) != null) {
				int separator = line.indexOf(" ");

				if (separator >= 0) {
					String firstName = line.substring(0,separator);
					String lastName = line.substring(separator+1);

					nameList.add(new Name(firstName, lastName));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return nameList;
	}

	/**
	 * Loads a list of strings from a specified filepath.
	 * If an error occurs, an empty list is returned.
	 * @param filePath of file to read form
	 * @return list of strings
	 */
	public static List<String> loadStrings(String filePath) {
		List<String> stringList = new ArrayList<>(1000);

		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
			String line;

			while ((line = bufferedReader.readLine()) != null) {
				stringList.add(line);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return stringList;
	}

}
