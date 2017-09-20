package ir.telgeram.Adel.MySettings;

import android.content.Context;
import android.content.res.Configuration;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import ir.telgeram.Adel.BaseFragmentAdapter;
import ir.telgeram.Adel.FontHelper;
import ir.telgeram.Adel.GhostPorotocol;
import ir.telgeram.Adel.SelectFontActivity;
import ir.telgeram.Adel.Setting;
import ir.telgeram.Adel.ThemeChanger;
import ir.telgeram.messenger.AndroidUtilities;
import ir.telgeram.messenger.LocaleController;
import ir.telgeram.messenger.R;
import ir.telgeram.ui.ActionBar.ActionBar;
import ir.telgeram.ui.ActionBar.BaseFragment;
import ir.telgeram.ui.Cells.EmptyCell;
import ir.telgeram.ui.Cells.HeaderCell;
import ir.telgeram.ui.Cells.ShadowSectionCell;
import ir.telgeram.ui.Cells.TextCheckCell;
import ir.telgeram.ui.Cells.TextSettingsCell;
import ir.telgeram.ui.Components.LayoutHelper;
import ir.telgeram.ui.DialogsActivity;

public class MySettingsActivity extends BaseFragment
{

	private ListView    listView;
	private ListAdapter listAdapter;

	private int rowCount;
	private int newgeramsectionrow;
	private int newgeramsectionrow2;
	private int Graphicsectionrow;
	private int Graphicsectionrow2;

	private int sgroup;
	private int ngroup;
	private int contact;
	private int all;

	@Override
	public boolean onFragmentCreate()
	{
		super.onFragmentCreate();

		rowCount = 0;
		newgeramsectionrow = rowCount++;
		newgeramsectionrow2 = rowCount++;

		ghostactivate = rowCount++;
		sendtyping = rowCount++;
		sendDeliver = rowCount++;
		Graphicsectionrow = rowCount++;
		channel = rowCount++; // Adel
		unread = rowCount++; // Adel
		bot = rowCount++; // Adel
		favor = rowCount++; // Adel

		return true;
	}

	@Override
	public View createView(final Context context)
	{

		actionBar.setBackgroundColor(ThemeChanger.getcurrent().getActionbarcolor());
		actionBar.setItemsBackgroundColor(ThemeChanger.getcurrent().getActionbarcolor(), false);
		actionBar.setBackButtonImage(R.drawable.ic_ab_back);

		actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
		{
			@Override
			public void onItemClick(int id)
			{
				if (id == -1)
				{
					finishFragment();
				}
			}
		});
		actionBar.setTitle(LocaleController.getString("MySettings", R.string.MySettings));
		if (AndroidUtilities.isTablet())
		{
			actionBar.setOccupyStatusBar(false);
		}

		listAdapter = new ListAdapter(context);

		fragmentView = new FrameLayout(context);
		FrameLayout frameLayout = (FrameLayout) fragmentView;

		listView = new ListView(context);
		listView.setDivider(null);
		listView.setDividerHeight(0);
		listView.setVerticalScrollBarEnabled(false);
		frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));
		listView.setAdapter(listAdapter);
		frameLayout.addView(actionBar);
		needLayout();

		return fragmentView;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (listAdapter != null)
		{
			listAdapter.notifyDataSetChanged();
		}
		fixLayout();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		fixLayout();
	}

	private void needLayout()
	{
		FrameLayout.LayoutParams layoutParams;
		int                      newTop = (actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
		if (listView != null)
		{
			layoutParams = (FrameLayout.LayoutParams) listView.getLayoutParams();
			if (layoutParams.topMargin != 0)
			{
				layoutParams.topMargin = 0;
				listView.setLayoutParams(layoutParams);
				//   extraHeightView.setTranslationY(newTop);
			}
		}
	}

	private void fixLayout()
	{
		if (fragmentView == null)
		{
			return;
		}
		fragmentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
		{
			@Override
			public boolean onPreDraw()
			{
				if (fragmentView != null)
				{
					needLayout();
					fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
				}
				return true;
			}
		});
	}

	private class ListAdapter extends BaseFragmentAdapter
	{
		private Context mContext;

		public ListAdapter(Context context)
		{
			mContext = context;
		}

		@Override
		public boolean areAllItemsEnabled()
		{
			return false;
		}

		@Override
		public boolean isEnabled(int i)
		{
			return i == drawStatus || i == DefaultFont || i == TabisUpside || i == ghostactivate || i == sendtyping || i == sendDeliver || i == favor || i == bot || i == all || i == channel || i == contact || i == ngroup || i == sgroup || i == unread;
		}

		@Override
		public int getCount()
		{
			return rowCount;
		}

		@Override
		public Object getItem(int i)
		{
			return null;
		}

		@Override
		public long getItemId(int i)
		{
			return i;
		}

		@Override
		public boolean hasStableIds()
		{
			return false;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup)
		{
			int type = getItemViewType(i);
			if (type == 0)
			{
				if (view == null)
				{
					view = new EmptyCell(mContext);
				}
			}
			else if (type == 1)
			{
				if (view == null)
				{
					view = new ShadowSectionCell(mContext);
				}
			}
			else if (type == 2)
			{
				if (view == null)
				{
					view = new TextSettingsCell(mContext);
				}
				TextSettingsCell textCell = (TextSettingsCell) view;
				if (i == DefaultFont)
				{
					textCell.setTextAndValue(LocaleController.getString("SelectFont", R.string.SelectFont), FontHelper.getFontTitle(Setting.getCurrentFont()), true);
				}
			}
			else if (type == 3)
			{
				if (view == null)
				{
					view = new TextCheckCell(mContext);
				}
				TextCheckCell textCell = (TextCheckCell) view;

				if (i == ghostactivate)
				{
					textCell.setTextAndCheck(LocaleController.getString("GhostMode", R.string.GhostMode), Setting.getGhostMode(), true);
				}
				else if (i == TabisUpside)
				{
					textCell.setTextAndCheck(LocaleController.getString("TabIsUp", R.string.TabIsUp), Setting.getTabIsUp(), true);
				}
				else if (i == sendtyping)
				{
					textCell.setTextAndCheck(LocaleController.getString("HideTypingState", R.string.HideTypingState), Setting.getSendTyping(), true);
				}
				else if (i == sendDeliver)
				{
					textCell.setTextAndCheck(LocaleController.getString("SendDeliverCheck", R.string.SendDeliverCheck), Setting.getsendDeliver(), true);
				}
				else if (i == favor)
				{
					textCell.setTextAndCheck(LocaleController.getString("Favorites", R.string.Favorites), Setting.TabisShowed("favor"), true);
				}
				else if (i == all)
				{
					textCell.setTextAndCheck(LocaleController.getString("AllChats", R.string.AllChats), Setting.TabisShowed("all"), true);
				}
				else if (i == channel)
				{
					textCell.setTextAndCheck(LocaleController.getString("Channels", R.string.Channels), Setting.TabisShowed("channel"), true);
				}
				else if (i == sgroup)
				{
					textCell.setTextAndCheck(LocaleController.getString("SuperGroups", R.string.SuperGroups), Setting.TabisShowed("sgroup"), true);
				}
				else if (i == ngroup)
				{
					textCell.setTextAndCheck(LocaleController.getString("Groups", R.string.Groups), Setting.TabisShowed("ngroup"), true);
				}
				else if (i == bot)
				{
					textCell.setTextAndCheck(LocaleController.getString("Bot", R.string.Bot), Setting.TabisShowed("bot"), true);
				}
				else if (i == contact)
				{
					textCell.setTextAndCheck(LocaleController.getString("Contacts", R.string.Contacts), Setting.TabisShowed("contact"), true);
				}
				else if (i == unread)
				{
					textCell.setTextAndCheck(LocaleController.getString("Unread", R.string.Unread), Setting.TabisShowed("unread"), true);
				}
				else if (i == drawStatus)
				{
					textCell.setTextAndCheck(LocaleController.getString("DrawStatus", R.string.DrawStatus), Setting.getDrawStatus(), true);
				}
			}
			else if (type == 4)
			{
				if (view == null)
				{
					view = new HeaderCell(mContext);
				}
				if (i == newgeramsectionrow2)
				{
					((HeaderCell) view).setText(LocaleController.getString("GhostProtocolSetting", R.string.GhostProtocolSetting));
				}
				else if (i == Graphicsectionrow2)
				{
					((HeaderCell) view).setText(LocaleController.getString("GraphicSetting", R.string.GraphicSetting));
				}
			}
			return view;
		}

		@Override
		public int getItemViewType(int i)
		{
			if (i == newgeramsectionrow || Graphicsectionrow == i)
			{
				return 1;
			}
			else if (drawStatus == i || TabisUpside == i || sendDeliver == i || ghostactivate == i || i == sendtyping || favor == i || bot == i || all == i || contact == i || sgroup == i || ngroup == i || unread == i || channel == i)
			{
				return 3;
			}
			else if (i == DefaultFont)
			{
				return 2;
			}
			else if (newgeramsectionrow2 == i || i == Graphicsectionrow2)
			{
				return 4;
			}
			else
			{
				return 2;
			}
		}

		@Override
		public int getViewTypeCount()
		{
			return 7;
		}

		@Override
		public boolean isEmpty()
		{
			return false;
		}
	}
}
