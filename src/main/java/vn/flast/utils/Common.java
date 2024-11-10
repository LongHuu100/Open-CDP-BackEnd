package vn.flast.utils;

import vn.flast.security.UserPrinciple;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.Normalizer;
import java.util.Collection;
import java.util.Optional;
import java.util.regex.Pattern;

public class Common {

    public static String deAccent(String str) {
        String nStr = str
            .replaceAll("Đ", "D")
            .replaceAll("đ", "")
            .replaceAll(" ", "-");
        String nfdNormalizedString = Normalizer.normalize(nStr, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).toString();
    }

    public static String escape(String s){
        return s.replace("\\", "\\\\")
            .replace("\t", "\\t")
            .replace("\b", "\\b")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\f", "\\f")
            .replace("\"", "\\\"");
    }

    private static UserPrinciple getInfo() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrinciple userPrinciple) {
            return userPrinciple;
        }
        return null;
    }

    public static String getSsoId () {
        return Optional.ofNullable(getInfo()).map(UserPrinciple::getSsoId).orElse("");
    }

    public static boolean CollectionIsEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
