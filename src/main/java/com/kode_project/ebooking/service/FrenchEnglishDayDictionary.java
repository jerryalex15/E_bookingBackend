package com.kode_project.ebooking.service;

import com.kode_project.ebooking.enums.JourSemaine;

import java.time.DayOfWeek;
import java.util.Map;

public class FrenchEnglishDayDictionary {
    public static final Map<DayOfWeek, JourSemaine> DAY_OF_WEEK_MAP = Map.of(
            DayOfWeek.MONDAY, JourSemaine.Lundi,
            DayOfWeek.TUESDAY, JourSemaine.Mardi,
            DayOfWeek.WEDNESDAY, JourSemaine.Mercredi,
            DayOfWeek.THURSDAY, JourSemaine.Jeudi,
            DayOfWeek.FRIDAY, JourSemaine.Vendredi,
            DayOfWeek.SATURDAY, JourSemaine.Samedi,
            DayOfWeek.SUNDAY, JourSemaine.Dimanche
    );
}
