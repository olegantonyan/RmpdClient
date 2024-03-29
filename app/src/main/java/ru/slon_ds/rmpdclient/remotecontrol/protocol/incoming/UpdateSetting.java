package ru.slon_ds.rmpdclient.remotecontrol.protocol.incoming;

import android.app.AlarmManager;
import android.content.Context;

import java.util.TimeZone;

import ru.slon_ds.rmpdclient.AndroidApplication;
import ru.slon_ds.rmpdclient.remotecontrol.ControlWrapper;
import ru.slon_ds.rmpdclient.utils.JsonDict;
import ru.slon_ds.rmpdclient.common.Logger;

public class UpdateSetting extends BaseCommand {
    public UpdateSetting(ControlWrapper control_wrapper, JsonDict data, Integer sequence_number) {
        super(control_wrapper, data, sequence_number);
    }

    @Override
    public boolean call() {
        String msg = "";
        boolean ok = true;
        String tz = get_data().fetch("time_zone", String.class);
        if (tz != null) {
            try {
                String tz_name = get_data().fetch("time_zone_name", String.class, null);
                String suitable_tz = suitable_timezone(tz, tz_name);
                if (suitable_tz == null) {
                    msg = "error setting timezone to " + tz + " (no suitable timezones found at this offset)";
                    ok = false;
                } else {
                    change_timezone(suitable_tz);
                    msg = "timezone set to " + tz + " (" + suitable_tz + ")";
                }
            } catch (Exception e) {
                msg = "error changing timezone";
                Logger.exception(this, msg, e);
                ok = false;
            }
        }
        return ack(ok, msg);
    }

    private void change_timezone(String tz) {
        Logger.info(this, "changing timezone to '" + tz + "'");
        AlarmManager am = (AlarmManager) AndroidApplication.context().getSystemService(Context.ALARM_SERVICE);
        am.setTimeZone(tz);
    }

    private String suitable_timezone(String offset, String tz_name) {
        if (tz_name != null) {
            tz_name = tz_name.toLowerCase();
            String all_timezones[] = TimeZone.getAvailableIDs();
            for (String t : all_timezones) {
                final String t_lower = t.toLowerCase();
                if (tz_name.contains(t_lower) || t_lower.contains(tz_name)) {
                    return t;
                }
            }
        }
        String suitable_timezones[] = TimeZone.getAvailableIDs(timezone_milliseconds_offset(offset));
        if (suitable_timezones.length == 0) {
            return null;
        }
        return suitable_timezones[0];
    }

    private int timezone_milliseconds_offset(String tz) {
        String sign = String.valueOf(tz.charAt(0));
        String offset_only = tz.substring(1);
        String parts[] = offset_only.split(":");
        String hours_string = parts[0];
        String minutes_string = parts[1];
        int hours = Integer.parseInt(hours_string);
        int minutes = Integer.parseInt(minutes_string);
        int offset_seconds = (hours * 60 + minutes) * 60;
        if (sign.equals("-")) {
            offset_seconds = -offset_seconds;
        }
        return offset_seconds * 1000;
    }
}
