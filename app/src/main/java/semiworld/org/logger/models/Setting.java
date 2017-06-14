/*
 *                    GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 ******************************************************************************/

package semiworld.org.logger.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created on 13.06.2017.
 */

@Table(name = "_Setting")
public class Setting extends Model {
    @Column(name = "_passActivated")
    public boolean passActivated = false;

    @Column(name = "_password")
    public String password;

    @Column(name = "_duration")
    public int duration = 3;

    public Setting() {
    }

    public Setting(boolean passActivated, String password, int duration) {
        this.passActivated = passActivated;
        this.password = password;
        this.duration = duration;
    }
}
