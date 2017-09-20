package ir.telgeram.Adel;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import ir.telgeram.messenger.ApplicationLoader;
import ir.telgeram.messenger.R;
import ir.telgeram.messenger.exoplayer2.util.NalUnitUtil;
import ir.telgeram.ui.ActionBar.ActionBar;
import ir.telgeram.ui.DialogsActivity;

public class ThemeChanger
{
	public static ActionBar actionbar;
	private static ArrayList<View>  views  = new ArrayList<>();
	private static ArrayList<Theme> themes = new ArrayList<>();
	private static Theme     currenttheme;
	private static ImageView floatingbutton;
	private static View      TabHost;

	public static void setcurrent(Theme t)
	{
		currenttheme = t;
		Setting.setTheme(t.getId());
	}

	public static Theme getcurrent()
	{
		return new Theme(0, "s", Setting.getActionbarcolor(), Setting.getTabcolor());
	}

	public static void addView(View view)
	{
		views.add(view);
	}

	public static ArrayList<Theme> LoadThemes()
	{
		if (themes != null && themes.size() > 0)
		{
			return themes;
		}
		themes = ThemeHelper.getThemes();
		return themes;
	}

	public static void ChangeTheme()
	{
		ActionBar.ChangeColor();

		for (int i = 0; i < views.size(); i++)
		{
			if (views == null)
			{
				views.remove(i);
				i--;

			}
			else
			{
				views.get(i).setBackgroundColor(Setting.getActionbarcolor());
			}
		}

		try
		{
			TabHost.setBackgroundColor(Setting.getTabcolor());

			if (actionbar != null)
			{
				actionbar.changeGhostModeVisibility();
			}
			ThemeChanger.floatingbutton.setBackgroundDrawable(getFloating(Setting.getActionbarcolor()));
		} catch (Exception e)
		{

		}
	}

	public static void EditActionbar(View actionbar)
	{
		actionbar.setBackgroundColor(Setting.getActionbarcolor());
	}

	public static void setFloatingbutton(ImageView floatingbutton)
	{
		ThemeChanger.floatingbutton = floatingbutton;
	}

	public static Drawable getFloating(int color)
	{
		int      to = color;
		Drawable d  = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.floating3_profile);
		try
		{
			d.setColorFilter(to, PorterDuff.Mode.MULTIPLY);
		} catch (Exception e)
		{

		}
		return d;
	}

	public static void settabhost(TabLayout tabhost)
	{
		ThemeChanger.TabHost = tabhost;
	}

	public static int ColorFilter(int i, int i2)
	{
		int alpha = Color.alpha(i);
		int red   = Color.red(i) - i2;
		int green = Color.green(i) - i2;
		int blue  = Color.blue(i) - i2;
		if (i2 < 0)
		{
			if (red > NalUnitUtil.EXTENDED_SAR)
			{
				red = NalUnitUtil.EXTENDED_SAR;
			}
			if (green > NalUnitUtil.EXTENDED_SAR)
			{
				green = NalUnitUtil.EXTENDED_SAR;
			}
			if (blue > NalUnitUtil.EXTENDED_SAR)
			{
				blue = NalUnitUtil.EXTENDED_SAR;
			}
			if (red == NalUnitUtil.EXTENDED_SAR && blue == NalUnitUtil.EXTENDED_SAR && red == NalUnitUtil.EXTENDED_SAR)
			{
				blue = i2;
				green = i2;
				red = i2;
			}
		}
		if (i2 > 0)
		{
			if (red < 0)
			{
				red = 0;
			}
			if (green < 0)
			{
				green = 0;
			}
			if (blue < 0)
			{
				blue = 0;
			}
			if (red == 0 && green == 0 && blue == 0)
			{
				blue = i2;
				green = i2;
				return Color.argb(alpha, green, blue, i2);
			}
		}
		i2 = blue;
		blue = green;
		green = red;
		return Color.argb(alpha, green, blue, i2);
	}
}
