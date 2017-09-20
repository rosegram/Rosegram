package ir.telgeram.Adel;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.readystatesoftware.viewbadger.BadgeView;

import java.util.ArrayList;

import ir.telgeram.messenger.AndroidUtilities;
import ir.telgeram.messenger.LocaleController;
import ir.telgeram.messenger.MessagesController;
import ir.telgeram.messenger.R;
import ir.telgeram.tgnet.TLRPC;

public class TabSetting
{
	private static ArrayList<TabModel> l;
	private static ArrayList<BadgeView> badges = new ArrayList<>();
	private static boolean justrunned;

	public static void init()
	{
		l = new ArrayList<>();
		badges = new ArrayList<>();
		justrunned = false;
	}

	public static ArrayList<TabLayout.Tab> GetTabs(final TabLayout tabHost, final Context context)
	{
		ArrayList<TabLayout.Tab> m = new ArrayList<>();
		ArrayList<TabModel>      l = getTabModels();

		for (int i = 0; i < l.size(); i++)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View           chatTab  = inflater.inflate(R.layout.tab_customview, null);
			ImageView      Img      = (ImageView) chatTab.findViewById(R.id.img);

			Img.setImageResource(l.get(i).getUnselectedicon());
			Img.setScaleType(ImageView.ScaleType.FIT_CENTER);

			TabLayout.Tab t = tabHost.newTab().setCustomView(Img);
			m.add(t);
			tabHost.addTab(t, false);

			BadgeView badge = new BadgeView(context, tabHost.getTabAt(i).getCustomView());
			badge.setText("0");
			badge.setPadding(5, 0, 5, 0);
			badge.setTextSize(11);
			badge.setBadgeBackgroundColor(0xff4ECC5E);
			badge.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
			badge.setBadgePosition(BadgeView.POSITION_BOTTOM_RIGHT);
			badge.hide();
			badges.add(badge);
		}
		return m;
	}

	public static void setBadges()
	{
		if (justrunned)
		{
			return;
		}

		justrunned = true;

		ChangeBadge(GetTAbPostition("favor"), TabSetting.CountUnread(MessagesController.getInstance().dialogsFavoriteOnly));
		ChangeBadge(GetTAbPostition("bot"), TabSetting.CountUnread(MessagesController.getInstance().dialogsBotOnly));
		ChangeBadge(GetTAbPostition("unread"), TabSetting.CountUnread(MessagesController.getInstance().dialogsUnreadOnly));
		ChangeBadge(GetTAbPostition("channel"), TabSetting.CountUnread(MessagesController.getInstance().dialogsChannelOnly));
		//		ChangeBadge(GetTAbPostition("sgroup"), TabSetting.CountUnread(MessagesController.getInstance().dialogsSuperGroupsOnly));
		ChangeBadge(GetTAbPostition("ngroup"), TabSetting.CountUnread(MessagesController.getInstance().dialogsJustGroupsOnly)); // Adel
		ChangeBadge(GetTAbPostition("contact"), TabSetting.CountUnread(MessagesController.getInstance().dialogsContactOnly));
		ChangeBadge(GetTAbPostition("all"), TabSetting.CountUnread(MessagesController.getInstance().dialogs));

		justrunned = false;
	}

	private static void ChangeBadge(int id, Unread unread)
	{
		if (id == -1)
		{
			return;
		}

		if (badges.size() > id)
		{
			badges.get(id).show();
			badges.get(id).setText(unread.Count > 999 ? "+999" : "" + unread.Count);

			if (unread.Count == 0) // hide
			{
				badges.get(id).hide();
			}
			else // show
			{
				if (unread.hasUnmute)
				{
					badges.get(id).setBadgeBackgroundColor(0xff4ECC5E);
				}
				else
				{
					badges.get(id).setBadgeBackgroundColor(Color.GRAY);
				}

				badges.get(id).setBackgroundDrawable(null);
				badges.get(id).show();
			}
		}
	}

	private static Unread CountUnread(ArrayList<TLRPC.TL_dialog> dialogs)
	{
		Unread unread = new Unread();
		for (int i = 0; i < dialogs.size(); i++)
		{
			TLRPC.TL_dialog dialog = dialogs.get(i);

			if (!HiddenController.isHidden(dialog.id) && dialog.unread_count > 0)
			{
				unread.Count += dialog.unread_count;

				if (!unread.hasUnmute && !MessagesController.getInstance().isDialogMuted(dialog.id))
				{
					unread.hasUnmute = true;
				}
			}
		}

		return unread;
	}

	// ------------------------------------------------------------------------------
	public static void SetTabIcon(TabLayout.Tab tab, int icon)
	{
		((ImageView) tab.getCustomView().findViewById(R.id.img)).setImageResource(icon);
	}

	public static int getNormalIcon(int id)
	{
		return l.get(id).getUnselectedicon();
	}

	public static int getSelectedICon(int id)
	{
		return l.get(id).getSelectedicon();
	}

	private static int GetTAbPostition(String name)
	{
		ArrayList<TabModel> s = getTabModels();
		for (int i = 0; i < s.size(); i++)
		{
			if (s.get(i).getId().toLowerCase().equals(name.toLowerCase()))
			{
				return i;
			}
		}
		return -1;
	}

	public static ArrayList<TabModel> getTabModels()
	{
		l = new ArrayList<>();

		if (LocaleController.getInstance().getCurrentLanguageName().equals("فارسی"))
		{
			if (Setting.TabisShowed("favor"))
			{
				l.add(new TabModel("favor", R.string.Favorites));
			}
			if (Setting.TabisShowed("bot"))
			{
				l.add(new TabModel("bot", R.string.Bot));
			}
			if (Setting.TabisShowed("unread"))
			{
				l.add(new TabModel("unread", R.string.Unread));
			}
			if (Setting.TabisShowed("channel"))
			{
				l.add(new TabModel("channel", R.string.Channels));
			}
			if (Setting.TabisShowed("ngroup"))
			{
				l.add(new TabModel("ngroup", R.string.Groups));
			}
			if (Setting.TabisShowed("contact"))
			{
				l.add(new TabModel("contact", R.string.Contacts));
			}
			if (Setting.TabisShowed("all"))
			{
				l.add(new TabModel("all", R.string.AllChats));
			}
		}
		else // English
		{
			if (Setting.TabisShowed("all"))
			{
				l.add(new TabModel("all", R.string.AllChats));
			}
			if (Setting.TabisShowed("contact"))
			{
				l.add(new TabModel("contact", R.string.Contacts));
			}
			if (Setting.TabisShowed("ngroup"))
			{
				l.add(new TabModel("ngroup", R.string.Groups));
			}
			if (Setting.TabisShowed("channel"))
			{
				l.add(new TabModel("channel", R.string.Channels));
			}
			if (Setting.TabisShowed("unread"))
			{
				l.add(new TabModel("unread", R.string.Unread));
			}
			if (Setting.TabisShowed("bot"))
			{
				l.add(new TabModel("bot", R.string.Bot));
			}
			if (Setting.TabisShowed("favor"))
			{
				l.add(new TabModel("favor", R.string.Favorites));
			}
			//		if (Setting.TabisShowed("sgroup"))
			//		{
			//			l.add(new TabModel("sgroup", R.string.SuperGroups));
			//		}
		}

		return l;
	}

	private static class Unread
	{
		public int     Count;
		public boolean hasUnmute;

		public Unread()
		{
			Count = 0;
			hasUnmute = false;
		}
	}
}
