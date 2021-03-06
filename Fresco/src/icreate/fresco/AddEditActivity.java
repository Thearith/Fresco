package icreate.fresco;

import icreate.fresco.Card.Side;
import icreate.fresco.Card.Type;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class AddEditActivity extends FragmentActivity implements OnTabChangeListener {

	public static final int GREEN  = 0xFF27AE60;
	public static final int ORANGE = 0xFFD35400;

	public static final String FRONT = "Front";
	public static final String BACK = "Back";
	
	Deck deck;

	private int deckID;
	private Card card;
	private boolean newEdit;
	private int positionColor;

	private int index;

	private FrontBackCardFragment frontFragment;
	private FrontBackCardFragment backFragment;

	private SqliteHelper database;

	private Type cardFrontType = Type.TEXT;
	private String cardFrontString = "";
	private Type cardBackType = Type.TEXT;
	private String cardBackString  = "";

	private Side side = Side.FRONT;
	private TabHost tabHost;

	public void setContent(String content) {
		switch(side) {
		case FRONT:
			cardFrontString = content;
			break;
		case BACK:
			cardBackString = content;
			break;
		}
	}

	public String getContent(Side side) {
		switch(side) {
		case FRONT:
			return cardFrontString;
		case BACK:
			return cardBackString;
		}
		return cardFrontString;
	}

	public void setType(Type type) {
		switch(side) {
		case FRONT:
			cardFrontType = type;
			break;
		case BACK:
			cardBackType = type;
			break;
		}
	}

	public Type getType(Side side) {
		switch(side) {
		case FRONT:
			return cardFrontType;
		case BACK:
			return cardBackType;
		}
		return cardFrontType;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_edit);
		Log.d("AddEditActivity", "onCreate");
		database = FrescoMain.getDatabase();

		Intent receiveIntent = getIntent();
		newEdit = receiveIntent.getBooleanExtra(Constant.NEW_EDIT, false);
		deckID  = receiveIntent.getIntExtra(Constant.DECK_ID, 1);
		index = receiveIntent.getIntExtra(Constant.INDEX, -1);
		positionColor = receiveIntent.getIntExtra(Constant.POSITION_COLOR, 0);
		side = getSide(receiveIntent.getBooleanExtra(Constant.SIDE, true));
		
		if( newEdit == true ) {
			int cardID = receiveIntent.getIntExtra(Constant.CARD_ID, 1);
			card = database.getCard(deckID, cardID);
		} else {
			card = new Card();
		} 

		initializeContent();
		initializeTabHost();

		deck = database.getDeck(deckID);
		
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(deck.getDeckName());
		int id = getResources().getIdentifier(deck.getDeckIcon(), "drawable", getPackageName());
		actionBar.setIcon(id);
		ImageView homeIcon = (ImageView) findViewById(android.R.id.home);
		homeIcon.setPadding(15, 0, 15, 0);
		actionBar.setDisplayHomeAsUpEnabled(true);

	}
	private void initializeContent() {
		cardFrontType = card.getType(Side.FRONT);
		cardFrontString = card.getContent(Side.FRONT);
		cardBackType = card.getType(Side.BACK);
		cardBackString  = card.getContent(Side.BACK);
	}

	private void initializeTabHost() {
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();

		tabHost.addTab(newTab(FRONT, FRONT, R.id.tab_front));
		tabHost.addTab(newTab(BACK, BACK, R.id.tab_back));

		if(side == Side.FRONT) {
			updateTabs(FRONT);
			tabHost.setCurrentTab(0);
		} else {
			updateTabs(BACK);
			tabHost.setCurrentTab(1);
		}
		tabHost.setOnTabChangedListener(this);
	}

	private void updateTabs(String tag) {
		FragmentManager fm = getSupportFragmentManager();
		Log.d("AddEditActivity", "updateTabs");
		switch(tag) {
		case FRONT:
			frontFragment = FrontBackCardFragment.createFragment(cardFrontString, getIntType(cardFrontType), true);
			fm.beginTransaction()
			.replace(R.id.tab_front, frontFragment, FRONT)
			.commit();
			break;
		case BACK:
			backFragment = FrontBackCardFragment.createFragment(cardBackString, getIntType(cardBackType), false);
			fm.beginTransaction()
			.replace(R.id.tab_back, backFragment, BACK)
			.commit();
			break;
		}
		changeTabColor();
	}

	private TabSpec newTab(String tag, String tagLabel, int contentId) {
		TabSpec tabSpec = tabHost.newTabSpec(tag);
		
		View tabTitle = getTabTitleView(tagLabel);
		
		tabSpec.setIndicator(tabTitle);
		tabSpec.setContent(contentId);
		return tabSpec;

	}

	private View getTabTitleView(String tagLabel) {
		LayoutInflater inflater = (LayoutInflater) 
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View tabTitle = inflater.inflate(R.layout.tab_title_layout, null);
		
		TextView tabTitleTextView = (TextView) tabTitle.findViewById(R.id.tabTitleTextView);
		tabTitleTextView.setText(tagLabel);
		
		ImageView tabTitleImageView = (ImageView) tabTitle.findViewById(R.id.tabTitleImageView);
		Side side = Side.FRONT;
		
		switch(tagLabel.toUpperCase()) {
			case "FRONT":
				side = Side.FRONT;
				break;
			case "BACK":
				side = Side.BACK;
				break;
		}
		
		setTabImageViewForCustomDialog(getType(side), tabTitleImageView);
		return tabTitle;
	}
	
	public View getTabTitleView() {
		
		View tabTitleView = tabHost.getTabWidget().getChildAt(0);
		
		if(side == Side.BACK) {
			tabTitleView = tabHost.getTabWidget().getChildAt(1);
		}
		
		Log.d("getTabTitleView ", String.valueOf(tabHost.getChildCount()));
		
		return tabTitleView;
	}

	private int getIntType(Type type) {
		switch(type) {
		case TEXT:
			return 0;
		case DOODLE:
			return 1;
		case IMAGE:
			return 2;
		case CAMERA:
			return 3;
		}

		return 0;
	}

	@Override
	public void onTabChanged(String tabId) {

		Log.d("AddEditActivity", "onTabChanged");
		
		switch(side) {
			case FRONT:
				cardFrontString = frontFragment.getContent();
				break;
			case BACK:
				cardBackString = backFragment.getContent();
				break;
		}
		
		switch(tabId) {
			case FRONT:	
				side = Side.FRONT;
				updateTabs(FRONT);
				break;
			case BACK:
				side = Side.BACK;
				updateTabs(BACK);
				break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_edit_activity, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.done_icon:
			confirmSaving();
			return true;

		case android.R.id.home:
			confirmLeaving();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void confirmLeaving() {
		AlertDialog.Builder exitDialog = new AlertDialog.Builder(AddEditActivity.this);

		exitDialog
		.setTitle("Exit Confirmation")
		.setCancelable(true)
		.setMessage("Changes not saved will be discarded")
		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				Intent sendIntent = new Intent(AddEditActivity.this, CardsViewPager.class);
				sendIntent.putExtra(Constant.DECK_ID, deckID);
				sendIntent.putExtra(Constant.INDEX, index);
				sendIntent.putExtra(Constant.POSITION_COLOR, positionColor);
				startActivity(sendIntent);
				finish();
			}
		});
		exitDialog
		.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		})
		.setIcon(android.R.drawable.ic_dialog_alert);

		AlertDialog dialog = exitDialog.create();
		dialog.show();
	}

	private void confirmSaving() {

		boolean isContentEmpty = false;

		saveCard();
		
		AlertDialog.Builder saveDialog = new AlertDialog.Builder(AddEditActivity.this);
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.add_card_custom_dialog, null);
		
		TextView savingMessageTextView = (TextView) view.findViewById(R.id.savingMessageTextView);
		savingMessageTextView.setText("Do you want to save and exit?");
		
		ImageView frontTabImageView = (ImageView) view.findViewById(R.id.frontTabImageView);
		ImageView backTabImageView = (ImageView) view.findViewById(R.id.backTabImageView);
		
		TextView specifyBackTextView = (TextView) view.findViewById(R.id.specifyBackTextView);
		TextView specifyFrontTextView = (TextView) view.findViewById(R.id.specifyFrontTextView);

		if(cardBackString.isEmpty()) {
			isContentEmpty = true;
			backTabImageView.setVisibility(View.GONE);
			savingMessageTextView.setText("Please add back card content");
			specifyBackTextView.setText("Empty");
			
		} else {
			Type backType = getType(Side.BACK);
			setTabImageViewForCustomDialog(backType, backTabImageView, specifyBackTextView);
		}
		
		if(cardFrontString.isEmpty()) {
			isContentEmpty = true;
			frontTabImageView.setVisibility(View.GONE);
			savingMessageTextView.setText("Please add front card content");
			specifyFrontTextView.setText("Empty");
			
		} else {
			Type frontType = getType(Side.FRONT);
			setTabImageViewForCustomDialog(frontType, frontTabImageView, specifyFrontTextView);
		}
		
		if(isContentEmpty == false) {
			
			saveDialog
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

					database = FrescoMain.getDatabase();
					if(newEdit == false) {
						database.insertCard(deckID, card);
					} else {
						database.updateCard(deckID, card);
					}

					Intent sendIntent = new Intent(AddEditActivity.this, CardsViewPager.class);
					sendIntent.putExtra(Constant.DECK_ID, deckID);
					sendIntent.putExtra(Constant.POSITION_COLOR, positionColor);
					if(newEdit == false)
						index++;
					sendIntent.putExtra(Constant.INDEX, index);
					startActivity(sendIntent);
					finish();
				}

			});

			saveDialog
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
		} else {
			saveDialog.setNeutralButton("Okay", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
		}

		saveDialog.setCustomTitle(view);
		saveDialog
		.setTitle("Save confirmation")
		.setIcon(android.R.drawable.ic_dialog_info);
		AlertDialog dialog = saveDialog.create();
		dialog.show();
	}

	private void setTabImageViewForCustomDialog(Type type,
			ImageView tabImageView, TextView specifyTextView) {
		switch(type) {
			case TEXT:
				tabImageView.setImageResource(R.drawable.text_24);
				specifyTextView.setText("(text)");
				break;
			case DOODLE:
				tabImageView.setImageResource(R.drawable.edit_24);
				specifyTextView.setText("(doodle)");
				break;
			case IMAGE:
				tabImageView.setImageResource(R.drawable.gallery_24);
				specifyTextView.setText("(gallery)");
				break;
			case CAMERA:
				tabImageView.setImageResource(R.drawable.camera_24);
				specifyTextView.setText("(camera)");
				break;
		}
	}
	
	private void setTabImageViewForCustomDialog(Type type,
			ImageView tabImageView) {
		switch(type) {
			case TEXT:
				tabImageView.setImageResource(R.drawable.text_24_white);
				break;
			case DOODLE:
				tabImageView.setImageResource(R.drawable.edit_24_white);
				break;
			case IMAGE:
				tabImageView.setImageResource(R.drawable.gallery_24_white);
				break;
			case CAMERA:
				tabImageView.setImageResource(R.drawable.camera_24_white);
				break;
		}
	}

	private void saveCard() {
		Log.d("AddEditActivity", "saveCard");
		saveCardType();
		card.setType(Side.FRONT, cardFrontType);
		card.setType(Side.BACK, cardBackType);
		saveCardContent();
		card.setContent(Side.FRONT, cardFrontString);
		card.setContent(Side.BACK, cardBackString);
	}

	private void saveCardContent() {
		if(frontFragment != null) 
			cardFrontString = frontFragment.getContent();
		if(backFragment != null)
			cardBackString = backFragment.getContent();
	}

	private void saveCardType() {
		if(frontFragment != null)
			cardFrontType = frontFragment.getType();
		if(backFragment != null)
			cardBackType = backFragment.getType();
	}


	private void changeTabColor() {
		if(side == Side.FRONT) {
			tabHost.getTabWidget().getChildAt(0).setBackgroundColor(GREEN);
			tabHost.getTabWidget().getChildAt(1).setBackgroundColor(ORANGE);
		} else {
			tabHost.getTabWidget().getChildAt(1).setBackgroundColor(GREEN);
			tabHost.getTabWidget().getChildAt(0).setBackgroundColor(ORANGE);
		}
	}

	private Side getSide(boolean isFront) {
		if(isFront)
			return Side.FRONT;
		return Side.BACK;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("AddEditActivity", "onActivityResult");
		if(frontFragment != null)
			frontFragment.onActivityResult(requestCode, resultCode, data);
		else
			backFragment.onActivityResult(requestCode, resultCode, data);
	}

	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		Log.d("AddEditActivity", "onSaveInstanceState");
	}
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		Log.d("AddEditActivity", "onRestoreInstanceState");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("AddEditActivity", "onDestroy");
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(AddEditActivity.this, CardsViewPager.class);
		intent.putExtra(Constant.DECK_ID, deckID);
		intent.putExtra(Constant.INDEX, index);
		intent.putExtra(Constant.POSITION_COLOR, positionColor);
		startActivity(intent);
		finish();
		//super.onBackPressed();
	}
}
