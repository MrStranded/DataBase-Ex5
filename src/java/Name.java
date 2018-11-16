/**
 * Name class to store the first and last name of a person.
 */
public class Name {

	private String firstName = "", lastName = "";

	public Name(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	//-------------------------------------------------------------------------

	public boolean equals(Name other) {
		return firstName.equals(other.firstName) && lastName.equals(other.lastName);
	}

	@Override
	public String toString() {
		return firstName + " " + lastName;
	}

	//-------------------------------------------------------------------------

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
