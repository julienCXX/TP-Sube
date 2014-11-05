package ar.edu.itba.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class implementing a menu with multiple choices and error detection. Items
 * are ordered by alphabetical order on the code.
 */
public class Menu
{
	private final Map<String, MenuItem> items;
	private String introText = "";
	private final boolean isRoot;

	/**
	 * Creates an empty menu (root level).
	 */
	public Menu()
	{
		this.isRoot = true;
		items = new LinkedHashMap<>();
	}

	/**
	 * Creates an empty menu.
	 *
	 * @param isRoot <code>true</code> if this menu is the root menu or
	 * <code>false</code> otherwise
	 */
	public Menu(boolean isRoot)
	{
		items = new LinkedHashMap<>();
		this.isRoot = isRoot;
	}

	/**
	 * Creates an empty menu (root level), with a text shown at the beginning of
	 * the menu (at each display).
	 *
	 * @param text the text to show at the beginning of the menu.
	 */
	public Menu(String text)
	{
		this.isRoot = true;
		items = new LinkedHashMap<>();
		setIntroText(text);
	}

	/**
	 * Creates an empty menu, with a text shown at the beginning of the menu (at
	 * each display).
	 *
	 * @param text the text to show at the beginning of the menu.
	 * @param isRoot <code>true</code> if this menu is the root menu or
	 * <code>false</code> otherwise
	 */
	public Menu(String text, boolean isRoot)
	{
		items = new LinkedHashMap<>();
		setIntroText(text);
		this.isRoot = isRoot;
	}

	/**
	 * Sets the text shown at the beginning of the menu (at each display).
	 *
	 * @param text the text to be shown
	 */
	public final void setIntroText(String text)
	{
		if (text == null)
		{
			text = "";
		}
		{
			introText = text;
		}
	}

	/**
	 * Adds an item to the menu.
	 *
	 * @param mi the item to add (cannot be null)
	 */
	public void addMenuItem(MenuItem mi)
	{
		if (mi == null)
		{
			throw new NullPointerException("The item cannot be null");
		}
		items.put(mi.getCode(), mi);
	}

	/**
	 * Creates an item and adds it to the menu.
	 *
	 * @param code the code the user has to input, to select it (must not be
	 * null)
	 * @param name the name of the item (displayed alongside of the code) (must
	 * not be null)
	 * @param rmi the instance of RunnableMenuItem to use when this item is
	 * choosen (must not be null)
	 */
	public void addMenuItem(String code, String name, RunnableMenuItem rmi)
	{
		addMenuItem(new MenuItem(code, name, rmi));
	}

	private void printMenuLoop()
	{
		IO.printlnInfo(introText);
		IO.println("Choose an option typing its code and pressing “Return”");
		IO.println("Code -> item");
		IO.println();
		for (MenuItem mi : items.values())
		{
			IO.println(mi + "");
		}
		IO.println("\nTo " + (isRoot ? "exit" : "go to previous menu")
			+ ", write an empty string");
		IO.print("Your choice: ");
	}

	/**
	 * Starts the menu.
	 *
	 * @throws java.lang.Exception any issue that can occur from the item's
	 * execution
	 */
	public void run() throws Exception
	{
		String userEntry;
		MenuItem choosenItem;

		printMenuLoop();
		userEntry = IO.readLine();

		while (!userEntry.isEmpty())
		{
			IO.println();
			choosenItem = items.get(userEntry);
			if (choosenItem == null)
			{
				IO.printlnError("This choice does not exist");
				IO.pause();
			} else
			{
				IO.printlnInfo(choosenItem.getName());
				choosenItem.runItem();
			}
			IO.println();
			printMenuLoop();
			userEntry = IO.readLine();
		}
	}
}
