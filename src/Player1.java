import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author You
 *
 */
public class Player1 {
	// Add member variables as needed. You MAY NOT use static variables, or otherwise allow direct communication between
	// different instances of this class by any means; doing so will result in a score of 0.

	private Hand partnersHand;
	private ArrayList<Card> criticalCards;
	private Map<Integer, CardKnowledge> myPotentialCards;
	private Map<Integer, CardKnowledge> partnerPotentialCards;
	private Map<Card, Integer> numCards;
	private ArrayList<Integer> toPlay;
	private ArrayList<Card> played;
	private ArrayList<Card> nextUp;

	private int myChop;
	private int partnerChop;

	/**
	 * This default constructor should be the only constructor you supply.
	 */
	public Player1() {
		partnersHand = new Hand();
		criticalCards = new ArrayList<>();
		myPotentialCards = new HashMap<>();
		partnerPotentialCards = new HashMap<>();
		numCards = new HashMap<>();
		toPlay = new ArrayList<>();
		played = new ArrayList<>();
		nextUp = new ArrayList<>();

		myChop = 4;
		partnerChop = 5;

		for (int i = 0; i<5; i++){
			nextUp.add(new Card(i, 1));
		}

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

		//Decrement the count of the number of cards available for discarded card
		numCards.put(discard, numCards.get(discard)-1);

		//Update potential lists
		updatePartnerPotentialCards(disIndex);
		updateMyPotentialCards();

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

		//Decrement the count of the number of cards available for discarded card
		numCards.put(discard, numCards.get(discard)-1);

		//Update potential lists
		updateMyPotentialCards(disIndex);
		updatePartnerPotentialCards();

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

		//Decrement the count of the number of cards available for played card
		numCards.put(play, numCards.get(play)-1);

		//Update cards to be played next
		updateNextUp(boardState);

		//Update potential lists
		updatePartnerPotentialCards(playIndex);
		updateMyPotentialCards();

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
		//Decrement the count of the number of cards available for played card
		numCards.put(play, numCards.get(play)-1);

		//Update Cards to be played next
		if (wasLegalPlay){
			updateNextUp(boardState);
		}

		//Update potential lists
		updateMyPotentialCards(playIndex);
		updatePartnerPotentialCards();
	}



	/**
	 * This method runs whenever your partner gives you a hint as to the color of your cards.
	 * @param color The color hinted, from Colors.java: RED, YELLOW, BLUE, GREEN, or WHITE.
	 * @param indices The indices (from 0-4) in your hand with that color.
	 * @param partnerHand Your partner's current hand.
	 * @param boardState The state of the board after the hint.
	 */
	public void tellColorHint(int color, ArrayList<Integer> indices, Hand partnerHand, Board boardState) {
		//Removes other colors from indices and remove color from not indices
		for (int index : myPotentialCards.keySet()){
			if (indices.contains(index)){
				myPotentialCards.get(index).knowColor(color);
			} else {
				myPotentialCards.get(index).eliminateColor(color);
			}
		}
	}
	
	/**
	 * This method runs whenever your partner gives you a hint as to the numbers on your cards.
	 * @param number The number hinted, from 1-5.
	 * @param indices The indices (from 0-4) in your hand with that number.
	 * @param partnerHand Your partner's current hand.
	 * @param boardState The state of the board after the hint.
	 */
	public void tellNumberHint(int number, ArrayList<Integer> indices, Hand partnerHand, Board boardState) {
		//Removes other numbers from indices and remove number from not indices
		for (int index : myPotentialCards.keySet()){
			if (indices.contains(index)){
				myPotentialCards.get(index).knowValue(number);
			} else {
				myPotentialCards.get(index).eliminateValue(number);
			}
		}
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
		return "DISCARD 0 0";
	}

	/**
	 * Makes a deep copy of given map for potential cards
	 * @param map to be copied
	 * @return copied map
	 */
	private Map<Integer, CardKnowledge> copyMap(Map<Integer, CardKnowledge> map){
		Map<Integer, CardKnowledge> temp = new HashMap<>();
		for (Map.Entry<Integer, CardKnowledge> entry : map.entrySet()){
			temp.put(entry.getKey(), new CardKnowledge(new HashSet<>(entry.getValue().getOptions())));
		}
		return temp;
	}

	/**
	 * Updates your potential course list based on cards left and cards in partners hands
	 */
	private void updateMyPotentialCards(){
		for (Card card : numCards.keySet()){
			if (numCards.get(card) == 1){
				if (partnersHand.countMatches(card) > 0){
					for (int index : myPotentialCards.keySet()) {
						myPotentialCards.get(index).eliminateCard(card);
					}
				}
			} else if (numCards.get(card) == 0){
				for (int index : myPotentialCards.keySet()){
					myPotentialCards.get(index).eliminateCard(card);
				}
			}
		}

		for (int index : myPotentialCards.keySet()){
			if (myPotentialCards.get(index).numOptions() == 1){
				Card card = new ArrayList<>(myPotentialCards.get(index).getOptions()).get(index);
				if (numCards.get(card) == 1) {
					for (int num : myPotentialCards.keySet()) {
						if (num != index){
							myPotentialCards.get(num).eliminateCard(card);
						}
					}
				}
			}
		}
	}

	/**
	 * This shifts over your potential card list if you discarded and then calls updateMyPotentialCourses()
	 * @param index of discarded card
	 */
	private void updateMyPotentialCards(int index){
		for (int i=index; i>0; i--){
			myPotentialCards.put(i, myPotentialCards.get(i-1));
		}

		myPotentialCards.put(0, new CardKnowledge());

		updateMyPotentialCards();
	}

	/**
	 * Updates your partners potential course list based on cards left and cards in partners hands
	 */
	private void updatePartnerPotentialCards(){
		for (Card card : numCards.keySet()){
			if (numCards.get(card) == 0){
				for (int index : partnerPotentialCards.keySet()){
					partnerPotentialCards.get(index).eliminateCard(card);
				}
			}
		}

		for (int index : partnerPotentialCards.keySet()){
			if (partnerPotentialCards.get(index).numOptions() == 1){
				Card card = new ArrayList<>(partnerPotentialCards.get(index).getOptions()).get(index);
				if (numCards.get(card) == 1) {
					for (int num : partnerPotentialCards.keySet()) {
						if (num != index){
							partnerPotentialCards.get(num).eliminateCard(card);
						}
					}
				}
			}
		}
	}

	/**
	 * This shifts over your partners potential card list if they discarded/played and then calls updateMyPotentialCourses()
	 * @param index of discarded/played card
	 */
	private void updatePartnerPotentialCards(int index){
		for (int i=index; i>0; i--){
			partnerPotentialCards.put(i, partnerPotentialCards.get(i-1));
		}

		partnerPotentialCards.put(0, new CardKnowledge());

		updatePartnerPotentialCards();
	}

	/**
	 * updates cards to be played next, I.E. nextUp
	 * @param board current board state
	 */
	private void updateNextUp(Board board){

		ArrayList<Integer> tab = board.tableau;

		for (int i=0; i<5; i++){
			nextUp.set(i, new Card(i, tab.get(i)+1));
		}

	}

}
