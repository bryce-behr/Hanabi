

public class Card implements Comparable<Card> {
	public static final int MIN_VALUE = 1;
	public static final int MAX_VALUE = 5;

	public final int color;
	public final int value;
	
	public Card(int color, int value) {
		this.color = color;
		this.value = value;
	}
	
	public Card(Card c) {
		this.color = c.color;
		this.value = c.value;
	}
	
	@Override
	public String toString() {
		return Colors.suitColor(color) + " " + value;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Card)) {
			return false;
		}
		Card c = (Card) o;
		return (this.color == c.color) && (this.value == c.value);
	}

	@Override
	public int compareTo(Card c) {
		if (this.color != c.color) {
			return this.color - c.color;
		}
		return this.value - c.value;
	}

	@Override
	public int hashCode() {
		return value + (color + 1) * (MAX_VALUE + 1);
	}
}
