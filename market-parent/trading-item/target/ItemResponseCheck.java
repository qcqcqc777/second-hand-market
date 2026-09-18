import com.tekram.pojo.*;
import com.tekram.mapper.ItemMapper;
import com.tekram.service.impl.ItemServiceImpl;
import tools.jackson.databind.json.JsonMapper;
import java.lang.reflect.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public class ItemResponseCheck {
  static void check(boolean ok) { if (!ok) throw new AssertionError(); }
  public static void main(String[] args) throws Exception {
    var item = new Item(); item.setId(1001L); item.setUserId(7L);
    item.setIdleName("keyboard"); item.setIdlePrice(new BigDecimal("88.00"));
    item.setPictureList("[\"/uploads/a.png\",\"https://example.com/b.png\"]");
    item.setItemStatus((byte)1); item.setModerationStatus((byte)0);
    item.setCreateTime(LocalDateTime.of(2026,9,14,16,0));
    var seller = new ItemVO.SellerVO(); seller.setId(7L); seller.setNickname("seller");
    var mapper = (ItemMapper) Proxy.newProxyInstance(ItemMapper.class.getClassLoader(), new Class[]{ItemMapper.class}, (p,m,a) -> switch(m.getName()) {
      case "getItemById" -> ((Long)a[0]) == 1001 ? item : null;
      case "listSellersByIds" -> List.of(seller);
      case "listItems", "getItemsByUserId" -> List.of(item);
      default -> throw new UnsupportedOperationException();
    });
    var service = new ItemServiceImpl(); var json = JsonMapper.builder().build();
    for (var name : List.of("itemMapper", "objectMapper")) {
      var field = ItemServiceImpl.class.getDeclaredField(name); field.setAccessible(true);
      field.set(service, name.equals("itemMapper") ? mapper : json);
    }
    var vo = service.getItemById(1001L); var tree = json.readTree(json.writeValueAsString(vo));
    check(tree.get("pictureList").size() == 2 && !tree.has("imageIds") && tree.has("idleName") && !tree.has("price") && tree.get("id").isNumber());
    check(vo.getIdlePrice().equals(new BigDecimal("88.00")) && vo.getItemStatus() == 1);
    check(vo.getSeller().getNickname().equals("seller") && vo.getCreateTime().equals(item.getCreateTime()));
    check(service.getItemById(99L) == null);
    var page = service.listItems(1,20,null,null);
    check(page.getTotal() == 1 && page.getRows().get(0) instanceof ItemVO);
    com.github.pagehelper.PageHelper.clearPage();
    var mine = service.listMyItems(1,20,7L);
    check(mine.getTotal() == 1 && ((ItemVO)mine.getRows().get(0)).getPictureList().size() == 2);
    com.github.pagehelper.PageHelper.clearPage();
    for (String empty : Arrays.asList(null, "", "[]")) {
      item.setPictureList(empty); check(service.getItemById(1001L).getPictureList().isEmpty());
    }
    item.setPictureList("[123]");
    try { service.getItemById(1001L); throw new AssertionError(); } catch (IllegalArgumentException expected) {}
    System.out.println("PASS: detail/list response, image paths, empty images, invalid images, seller, price, status, timezone");
  }
}
