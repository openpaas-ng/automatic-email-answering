package org.linagora.intentDetection.entities;

public enum TimeUnit {
	
	second, minute, hour, day, week, month, year;
	
 public static TimeUnit getEquivalentTimeUnit(String word) {
		
		switch(word.toLowerCase()) {
		//french conversion
		case "seconde": return TimeUnit.second;
		case "secondes": return TimeUnit.second;
		case "minute": return TimeUnit.minute;
		case "minutes": return TimeUnit.minute;
		case "heure": return TimeUnit.hour;
		case "heures": return TimeUnit.hour;
		case "jour": return TimeUnit.day;
		case "jours": return TimeUnit.day;
		case "semaine": return TimeUnit.week;
		case "semaines": return TimeUnit.week;
		case "mois": return TimeUnit.month;
		case "année": return TimeUnit.year;
		case "années": return TimeUnit.year;
		
		//english conversion
		case "second": return TimeUnit.second;
		case "seconds": return TimeUnit.second;
		case "hour": return TimeUnit.hour;
		case "hours": return TimeUnit.hour;
		case "day": return TimeUnit.day;
		case "days": return TimeUnit.day;
		case "week": return TimeUnit.week;
		case "weeks": return TimeUnit.week;
		case "month": return TimeUnit.month;
		case "months": return TimeUnit.month;
		case "year": return TimeUnit.year;
		case "years": return TimeUnit.year;
		
		}
		
		return null;
	}

}
