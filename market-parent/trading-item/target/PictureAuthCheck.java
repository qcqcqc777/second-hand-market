import com.tekram.pojo.*;
import com.tekram.mapper.ItemMapper;
import com.tekram.service.impl.ItemServiceImpl;
import com.tekram.interceptor.LoginInterceptor;
import jakarta.servlet.http.*;
import tools.jackson.databind.json.JsonMapper;
import java.lang.reflect.*;
import java.util.*;
import java.io.*;
public class PictureAuthCheck {
  static void check(boolean ok) { if (!ok) throw new AssertionError(); }
  public static void main(String[] args) throws Exception {
    var json = JsonMapper.builder().build();
    var captured = new Item[1];
    var mapper = (ItemMapper) Proxy.newProxyInstance(ItemMapper.class.getClassLoader(), new Class[]{ItemMapper.class}, (p,m,a) -> {
      if (m.getName().equals("insertItem")) { captured[0]=(Item)a[0]; return 1; }
      throw new UnsupportedOperationException();
    });
    var service = new ItemServiceImpl();
    for (var name : List.of("itemMapper", "objectMapper")) {
      var f = ItemServiceImpl.class.getDeclaredField(name); f.setAccessible(true);
      f.set(service, name.equals("itemMapper") ? mapper : json);
    }
    var dto = new ItemDTO();
    for (var pictures : Arrays.asList(List.of("a,b.png", "quote\".png", "中文.png"), List.<String>of())) {
      dto.setPictureList(pictures); service.createItem(dto, 7L);
      check(json.readValue(captured[0].getPictureList(), List.class).equals(pictures));
      check(captured[0].getUserId() == 7L);
    }
    dto.setPictureList(null); service.createItem(dto, 7L);
    check(captured[0].getPictureList().equals("[]"));
    for (var context : List.of("", "/api")) {
      for (var route : List.of(new String[]{"GET","/items","true"},new String[]{"POST","/items","false"},new String[]{"GET","/items/me","false"},new String[]{"GET","/users/me","false"},new String[]{"OPTIONS","/items","true"})) {
        int[] status = {200};
        var req = (HttpServletRequest) Proxy.newProxyInstance(HttpServletRequest.class.getClassLoader(), new Class[]{HttpServletRequest.class}, (p,m,a) -> switch(m.getName()) {
          case "getMethod" -> route[0]; case "getRequestURI" -> context + route[1]; case "getContextPath" -> context; case "getHeader" -> null; default -> throw new UnsupportedOperationException(m.getName());
        });
        var res = (HttpServletResponse) Proxy.newProxyInstance(HttpServletResponse.class.getClassLoader(), new Class[]{HttpServletResponse.class}, (p,m,a) -> switch(m.getName()) {
          case "setStatus" -> { status[0]=(Integer)a[0]; yield null; }
          case "setContentType" -> null; case "getWriter" -> new PrintWriter(new StringWriter()); default -> throw new UnsupportedOperationException(m.getName());
        });
        boolean allowed = new LoginInterceptor().preHandle(req,res,new Object());
        check(allowed == Boolean.parseBoolean(route[2]));
        check(status[0] == (allowed ? 200 : 401));
      }
    }
    System.out.println("PASS: picture JSON round-trip, empty/null pictures, userId, GET/POST auth, protected paths, OPTIONS, context path");
  }
}
