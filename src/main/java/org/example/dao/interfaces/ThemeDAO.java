package org.example.dao.interfaces;

import org.example.entities.Theme;

import java.util.List;

public interface ThemeDAO {

    /**
     * Read theme from database by id.
     * @param id theme's id
     * @return Theme
     */
    Theme getTheme(int id);

    /**
     * Insert new theme into database.
     * @param theme adding theme
     */
    void addTheme(Theme theme);

    /**
     * Update theme's data into database.
     * @param theme editing theme
     */
    void updateTheme(Theme theme);

    /**
     * Delete theme from database.
     * @param id theme's id
     */
    void deleteTheme(int id);

    /**
     * Read themes from database by class and subject.
     * @param id subject details id
     * @return List<Theme>
     */
    List<Theme> getThemesBySubjectDetails(int id);
}
