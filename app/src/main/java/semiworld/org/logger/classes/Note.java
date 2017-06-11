/*
 *                    GNU GENERAL PUBLIC LICENSE
 *                        Version 3, 29 June 2007
 *
 *  Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 *  Everyone is permitted to copy and distribute verbatim copies
 *  of this license document, but changing it is not allowed.
 ******************************************************************************/

package semiworld.org.logger.classes;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created on 07.06.2017.
 */

// Our table that storage our data in database
// Should be extends from Model
// You may not define @Column attribute
@Table(name = "TBL_Note")
public class Note extends Model {
    @Column(name = "_Text")
    public String Text;
    @Column(name = "_Date")
    public java.util.Date Date;

    public Note() {
    }

    public Note(String text, java.util.Date date) {
        Text = text;
        Date = date;
    }
}
