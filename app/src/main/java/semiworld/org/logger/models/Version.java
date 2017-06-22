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
 * Created on 21.06.2017.
 */
@Table(name = "_Version")
public class Version extends Model {
    @Column(name = "_latest")
    public String latest="1.0.0";

    public Version() {
    }

    public Version(String latest) {
        this.latest = latest;
    }
}
