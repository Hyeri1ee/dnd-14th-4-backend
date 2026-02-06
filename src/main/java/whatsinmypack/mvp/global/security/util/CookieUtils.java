package whatsinmypack.mvp.global.security.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import org.springframework.core.serializer.DefaultDeserializer;
import org.springframework.core.serializer.DefaultSerializer;

public class CookieUtils {

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    public static String serialize(Object object) {
        if (object == null) return "";

        DefaultSerializer serializer = new DefaultSerializer();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            serializer.serialize(object, baos);
            return Base64.getUrlEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new IllegalArgumentException("객체 직렬화에 실패했습니다.", e);
        }
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        byte[] decode = Base64.getUrlDecoder().decode(cookie.getValue());
        DefaultDeserializer deserializer = new DefaultDeserializer();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(decode)) {
            Object object = deserializer.deserialize(bais);
            return cls.cast(object);
        } catch (IOException e) {
            throw new IllegalArgumentException("객체 역직렬화에 실패했습니다.", e);
        }
    }
}
