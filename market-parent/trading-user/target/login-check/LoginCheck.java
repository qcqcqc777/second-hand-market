import com.tekram.pojo.*;
import com.tekram.mapper.UserMapper;
import com.tekram.service.impl.AuthServiceImpl;
import com.tekram.controller.AuthController;
import com.tekram.util.JwtUtils;
import java.lang.reflect.Field;
public class LoginCheck {
 static class FakeMapper implements UserMapper {
  int calls; User result;
  public int addUser(User u) { throw new AssertionError("No DB writes"); }
  public User getLoginInfoByAccountAndPassward(User u) { calls++; return result; }
 }
 static void inject(Object o,String field,Object v) throws Exception { Field f=o.getClass().getDeclaredField(field); f.setAccessible(true); f.set(o,v); }
 static void check(boolean b) { if(!b) throw new AssertionError(); }
 public static void main(String[] args) throws Exception {
  var service=new AuthServiceImpl(); var mapper=new FakeMapper(); inject(service,"userMapper",mapper);
  check(service.login(null)==null); check(service.login(new User())==null); check(mapper.calls==0);
  User request=new User(); request.setAccountNumber("test-account"); request.setPasswordHash("test-only"); request.setAvatarUrl("untrusted-request-avatar");
  check(service.login(request)==null);
  User stored=new User(); stored.setId(123L); stored.setAccountNumber("test-account"); stored.setNickname("test"); stored.setAvatarUrl("stored-avatar"); stored.setUserStatus((byte)1); mapper.result=stored;
  check(service.login(request)==null); stored.setUserStatus((byte)0);
  var info=service.login(request); check(info.getAvatarUrl().equals("stored-avatar"));
  var claims=JwtUtils.parseJWT(info.getToken()); check(((Number)claims.get("id")).longValue()==123L); check(claims.getExpiration().getTime()>System.currentTimeMillis());
  String[] parts=info.getToken().split("\\.");
  boolean rejected=false;
  try { JwtUtils.parseJWT(parts[0]+"."+parts[1]+".AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"); } catch(io.jsonwebtoken.JwtException expected) { rejected=true; }
  check(rejected);
  var controller=new AuthController(); inject(controller,"authService",service); mapper.result=null; check(controller.login(request).getCode()==0);
  System.out.println("PASS: JWT generation/parsing/tamper rejection; blank input; invalid credentials; banned account; stored avatar; failure response. No database requests.");
 }
}
