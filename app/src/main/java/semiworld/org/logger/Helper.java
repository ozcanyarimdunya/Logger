/*
 *                    GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 ******************************************************************************/

package semiworld.org.logger;

import android.util.Log;

/**
 * Created on 08.06.2017.
 */

public class Helper {
    public static void log(String text) {
        Log.v("abcdef", String.valueOf(":: " + text));
    }
}
