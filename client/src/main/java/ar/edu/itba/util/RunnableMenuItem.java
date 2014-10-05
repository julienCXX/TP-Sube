package ar.edu.itba.util;

/**
 * The program to be executed when the corresponding item is choosen.
 */
public interface RunnableMenuItem
{
	/**
	 * Runs the said item.
	 *
	 * @throws Exception any issue that may occur, that this item won't handle
	 */
	public abstract void runItem() throws Exception;
}
