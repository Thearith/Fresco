package icreate.fresco;

import java.util.ArrayList;
import java.util.List;

public class Deck {
	private String _name;
	List<Card> _cards;
	int _deckID;
	String _deckIcon;
	
	public Deck() {
		_name = "";
		_cards = new ArrayList<Card>();
		_deckIcon = "";
	}
	
	public Deck(String name){
		_name = name;
		_cards = new ArrayList<Card>();
	}
	
	public String getDeckIcon(){
		return _deckIcon;
	}
	
	public void setDeckIcon(String icon){
		_deckIcon = icon;
	}
	
	public int getDeckID() {
		return _deckID;
	}
	
	public void setDeckID(int id) {
		_deckID = id;
	}
	
	public void setDeckName(String name) {
		_name = name;
	}
	
	public String getDeckName() {
		return _name;
	}
	
	public int getCardCount(){
		return _cards.size();
	}
	
	public Card get(int index){
		return _cards.get(index);
	}
	
	public Card getCard(int cardID) {
		for(Card card:_cards) {
			if(card.getCardID() == cardID)
				return card;
		}
		return null;
	}
	
	public List<Card> getAll(){
		return _cards;
	}
	
	public void addCard(Card card){
		_cards.add(card);
	}
	
	boolean isContain(int cardID) {
		for(Card card: _cards) {
			if(card.getCardID() == cardID) 
				return true;
		}
		return false;
	}
	
	@Override
	public String toString(){
		return _name + " (" + getCardCount() + ")";
	}
}
