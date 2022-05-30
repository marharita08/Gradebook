package org.example.dao.interfaces;

import org.example.entities.Theme;

import java.util.List;

public interface ThemeDAO {

    /**
     * Read theme from database by id.
     * @param id theme's id
     * @return Theme
     */
    Theme getTheme(int id, String dbName);

    /**
     * Insert new theme into database.
     * @param theme adding theme
     */
    void addTheme(Theme theme, String dbName);

    /**
     * Update theme's data into database.
     * @param theme editing theme
     */
    void updateTheme(Theme theme, String dbName);

    /**
     * Delete theme from database.
     * @param id theme's id
     */
    void deleteTheme(int id, String dbName);

    /**
     * Read themes from database by class and subject.
     * @param id subject details id
     * @return List<Theme>
     */
    List<Theme> getThemesBySubjectDetails(int id, String dbName);

    /**
     * Count themes from database by subject details.
     * @param id subject details id
     * @return int count
     */
    int getCountOfThemesBySubjectDetails(int id, String dbName);
}
