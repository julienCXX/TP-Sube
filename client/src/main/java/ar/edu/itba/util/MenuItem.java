package ar.edu.itba.util;

/**
 * Class representing a selectable item, in a menu.
 */
public class MenuItem
{
	private final String code, name;
	private final RunnableMenuItem runnable;

	/**
	 * Creates a new item.
	 *
	 * @param code the code the user has to input, to select it (must not be
	 * null)
	 * @param name the name of the item (displayed alongside of the code) (must
	 * not be null)
	 * @param runnable the instance of RunnableMenuItem to use when this item is
	 * choosen (must not be null)
	 */
	public MenuItem(String code, String name, RunnableMenuItem runnable)
	{
		this.code = code.trim().toLowerCase();
		this.name = name.trim();
		if (runnable == null)
		{
			throw new NullPointerException("Runnable cannot be null");
		}
		this.runnable = runnable;
	}

	/**
	 * Returns the code the user has to input.
	 *
	 * @return the code the user has to input
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * Returns the name of this item.
	 *
	 * @return the name of this item
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Runs the item's runnable.
	 *
	 * @throws Exception any exceptions coming from the runnable
	 */
	public void runItem() throws Exception
	{
		runnable.runItem();
	}

	@Override
	public String toString()
	{
		return getCode() + " -> " + getName();
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof MenuItem))
		{
			return false;
		}
		MenuItem mi = (MenuItem) o;
		return getCode().equals(mi.getCode());
	}

	@Override
	public int hashCode()
	{
		return getCode().hashCode();
	}
}
