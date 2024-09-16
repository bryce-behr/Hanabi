import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Bryce Behr
 * @author David Le Roux
 */
public class Player {
	// Add member variables as needed. You MAY NOT use static variables, or otherwise allow direct communication between
	// different instances of this class by any means; doing so will result in a score of 0.

	private ArrayList<CardKnowledge> your_options;// = new ArrayList<CardKnowledge>(5);
	private ArrayList<CardKnowledge> partner_options;// = new ArrayList<CardKnowledge>(5);

	private Set<Card> impossible_cards;// = new HashSet<Card>();

	private String action;// = "";
	private int index;// = -1;


	/**
	 * This default constructor should be the only constructor you supply.
	 */
	public Player() {
		your_options = new ArrayList<CardKnowledge>();
		partner_options = new ArrayList<CardKnowledge>();
		for(int i = 0; i<5; i++) your_options.add(new CardKnowledge());
		impossible_cards = new HashSet<Card>();
		action = "";
		index = -1;
	}

	/**
	 * This method runs whenever your partner discards a card.
	 * @param startHand The hand your partner started with before discarding.
	 * @param discard The card he discarded.
	 * @param disIndex The index from which he discarded it.
	 * @param draw The card he drew to replace it; null, if the deck is empty.
	 * @param drawIndex The index to which he drew it.
	 * @param finalHand The hand your partner ended with after redrawing.
	 * @param boardState The state of the board after play.
	 */
	public void tellPartnerDiscard(Hand startHand, Card discard, int disIndex, Card draw, int drawIndex,
								   Hand finalHand, Board boardState) {
		update_impossible_cards(boardState, get_hand(finalHand));
	}

	/**
	 * This method runs whenever you discard a card, to let you know what you discarded.
	 * @param discard The card you discarded.
	 * @param disIndex The index from which you discarded it.
	 * @param drawIndex The index to which you drew the new card (if drawSucceeded)
	 * @param drawSucceeded true if there was a card to draw; false if the deck was empty
	 * @param boardState The state of the board after play.
	 */
	public void tellYourDiscard(Card discard, int disIndex, int drawIndex, boolean drawSucceeded, Board boardState) {

	}

	/**
	 * This method runs whenever your partner played a card
	 * @param startHand The hand your partner started with before playing.
	 * @param play The card she played.
	 * @param playIndex The index from which she played it.
	 * @param draw The card she drew to replace it; null, if the deck was empty.
	 * @param drawIndex The index to which she drew the new card.
	 * @param finalHand The hand your partner ended with after playing.
	 * @param wasLegalPlay Whether the play was legal or not.
	 * @param boardState The state of the board after play.
	 */
	public void tellPartnerPlay(Hand startHand, Card play, int playIndex, Card draw, int drawIndex,
								Hand finalHand, boolean wasLegalPlay, Board boardState) {
		update_impossible_cards(boardState, get_hand(finalHand));
	}


	/**
	 * This method runs whenever you play a card, to let you know what you played.
	 * @param play The card you played.
	 * @param playIndex The index from which you played it.
	 * @param drawIndex The index to which you drew the new card (if drawSucceeded)
	 * @param drawSucceeded  true if there was a card to draw; false if the deck was empty
	 * @param wasLegalPlay Whether the play was legal or not.
	 * @param boardState The state of the board after play.
	 */
	public void tellYourPlay(Card play, int playIndex, int drawIndex, boolean drawSucceeded,
							 boolean wasLegalPlay, Board boardState) {
	}



	/**
	 * This method runs whenever your partner gives you a hint as to the color of your cards.
	 * @param color The color hinted, from Colors.java: RED, YELLOW, BLUE, GREEN, or WHITE.
	 * @param indices The indices (from 0-4) in your hand with that color.
	 * @param partnerHand Your partner's current hand.
	 * @param boardState The state of the board after the hint.
	 */
	public void tellColorHint(int color, ArrayList<Integer> indices, Hand partnerHand, Board boardState) {
		for(Integer der: indices) {
			your_options.get(der).knowColor(color);
		}

		action = "PLAY";
		index = indices.getFirst();
	}

	/**
	 * This method runs whenever your partner gives you a hint as to the numbers on your cards.
	 * @param number The number hinted, from 1-5.
	 * @param indices The indices (from 0-4) in your hand with that number.
	 * @param partnerHand Your partner's current hand.
	 * @param boardState The state of the board after the hint.
	 */
	public void tellNumberHint(int number, ArrayList<Integer> indices, Hand partnerHand, Board boardState) {
		for(Integer index: indices) {
			your_options.get(index).knowValue(number);
		}

		action = "PLAY";
		index = indices.getFirst();
	}

	/**
	 * This method runs when the game asks you for your next move.
	 * @param yourHandSize How many cards you have in hand.
	 * @param partnerHand Your partner's current hand.
	 * @param boardState The current state of the board.
	 * @return A string encoding your chosen action. Actions should have one of the following formats; in all cases,
	 *  "x" and "y" are integers.
	 * 	a) "PLAY x y", which instructs the game to play your card at index x and to draw a card back to index y. You
	 *     should supply an index y even if you know the deck to be empty. All indices should be in the range 0-4.
	 *     Illegal plays will consume a fuse; at 0 fuses, the game ends with a score of 0.
	 *  b) "DISCARD x y", which instructs the game to discard the card at index x and to draw a card back to index y.
	 *     You should supply an index y even if you know the deck to be empty. All indices should be in the range 0-4.
	 *     Discarding returns one hint if there are fewer than the maximum number available.
	 *  c) "NUMBERHINT x", where x is a value from 1-5. This command informs your partner which of his cards have a value
	 *     of the chosen number. An error will result if none of his cards have that value, or if no hints remain.
	 *     This command consumes a hint.
	 *  d) "COLORHINT x", where x is one of the RED, YELLOW, BLUE, GREEN, or WHITE constant values in Colors.java.
	 *     This command informs your partner which of his cards have the chosen color. An error will result if none of
	 *     his cards have that color, or if no hints remain. This command consumes a hint.
	 */
	public String ask(int yourHandSize, Hand partnerHand, Board boardState) {
		// A really dumb agent that just discards
		// TODO: replace this with your agent's decision-making code
		ArrayList<Card> partner_hand = get_hand(partnerHand);
		//always update impossible_cards
		update_impossible_cards(boardState, partner_hand);

		// if you've been given a hint
		if(action.equals("PLAY")) {
			return play_card(index, boardState);
		}

		// do something if you KNOW something about your hand
		for(int i = 0; i < your_options.size(); i++) {
			// this is your knowledge about a single card in your hand
			CardKnowledge knowledge = your_options.get(i);

			// if you have less than eights hints and have a card that can be discarded
			if(boardState.numHints < 8 && knowledge.isDiscardable(boardState)) return discard_card(i, boardState);
				// if playable, play the card and update the knowledge of your hand
			else if(knowledge.isDefinitelyPlayable(boardState)) return play_card(i, boardState);
		}

		//
		if(boardState.numHints>0){
			for (int i = 0; i < partner_hand.size(); i++) {
				Card card = partner_hand.get(i);
				if (boardState.isLegalPlay(card)) {
					boolean colorsBefore = false;
					boolean valuesBefore = false;

					for (int j = 0; j < i; j++) {
						if (card.color == partner_hand.get(j).color) colorsBefore = true;
					}

					for (int j = 0; j < i; j++) {
						if (card.value == partner_hand.get(j).value) valuesBefore = true;
					}


					if (!colorsBefore) {
						return hint_color(card.color, partner_hand);
					} else if (!valuesBefore) {

						return hint_value(card.value, partner_hand);
					}
				}

			}
		}
		return discard_card(yourHandSize-1, boardState);
	}

	// this method plays a card
	private String play_card(int card_loc, Board board) {
		action = "";
		index = -1;
		your_options.remove(card_loc);
		if(board.deckSize > 0) your_options.addFirst(new CardKnowledge(impossible_cards));
		return "PLAY " + card_loc + " 0";
	}

	// this method discards a card
	private String discard_card(int card_loc, Board board) {
		your_options.remove(card_loc);
		if(board.deckSize > 0) your_options.addFirst(new CardKnowledge(impossible_cards));
		return "DISCARD " + card_loc + " 0";
	}

	private String hint_color(int color, ArrayList<Card> partner_hand) {
		return "COLORHINT " + color;
	}

	private String hint_value(int value, ArrayList<Card> partner_hand) {
		return "NUMBERHINT " + value;
	}

	// this returns and arraylist version of a given hand
	// mostly used for convenience
	private ArrayList<Card> get_hand(Hand hand_obj) {
		ArrayList<Card> hand_list = new ArrayList<Card>();
		for(int i = 0; i < hand_obj.size(); i++) {
			hand_list.add(hand_obj.get(i));
		}
		return hand_list;
	}

	/* Go through each card and check if they've all been seen. If
	 * they have, add that card to impossible_cards */
	private void update_impossible_cards(Board board, ArrayList<Card> partner_hand) {
		for(int color = 0; color < 5; color++) {
			for(int value = 1; value <= 5; value++) {
				Card temp_card = new Card(color, value);
				if(cards_all_seen(temp_card, board, partner_hand)) impossible_cards.add(temp_card);
			}
		}
	}


	// this method counts how many cards in the discard pile or tableau
	// are the same as the given card
	private int count_cards_seen(Card card, Board board, ArrayList<Card> partner_hand) {
		int count = 0;

		// count matches in discards
		for(Card discarded_card: board.discards) {
			if(card.equals(discarded_card)) count++;
		}

		// inc for matches in tableau
		if(board.tableau.get(card.color) >= card.value) count++;

		// check each of partners hands
		for (Card value : partner_hand) {
			if (value.equals(card)) count++;
		}

		return count;
	}

	// this method tells you if all of a certain card have been seen
	private boolean cards_all_seen(Card card, Board board, ArrayList<Card> partner_hand) {
		int cards_seen = count_cards_seen(card, board, partner_hand);
		switch (card.value) {
			case 1:  if(cards_seen == 3) return true;
				break;
			case 2, 4, 3:  if(cards_seen == 2) return true;
				break;
			case 5:  if(cards_seen == 1) return true;
				break;
		}

		return false;
	}
}
