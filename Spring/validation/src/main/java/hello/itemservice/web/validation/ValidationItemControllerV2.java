package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

    @PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        //bindingResult 에 type이 잘못들어가는경우 504 에러가 아닌 bindingresult에 Exception message가 삽입된다.
        //검증오류 보관
//        Map<String, String> errors = new HashMap<>();//BindingResult객체로 대체, Argument 순서가 중요한다.(ModelAttribute 바로 옆)
        if(!StringUtils.hasText(item.getItemName())){//글자가 없다면(Null Check)
//            errors.put("itemName", "상품 명은 필수입니다.");//errors Map을 bindingResult로 매핑시킴
            bindingResult.addError(new FieldError("item", "itemName",item.getItemName(),false,new String[]{"required.item.itemName"},null, null)); //DTO에 value가 제대로 입력되지 않을경우 입력받은 오류 value를 그대로 출력 시키도록 해줌(field Error의 기능)
        }

        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
//            errors.put("price", "가격은 1000 ~ 1000000 사이입니다.");
            bindingResult.addError(new FieldError("item", "price",item.getPrice(),false,new String[]{"range.item.price"},new Object[]{1000, 10000}, null)); //4,5번째의 인자를 통해 errors.properties의 메세지와 argument를 넘겨줌
        }

        if(item.getQuantity() == null || item.getQuantity() >= 9999){
//            errors.put("quantity", "수량은 9999까지 허용합니다.");
            bindingResult.addError(new FieldError("item", "quantity",item.getQuantity(),false,new String[]{"max.item.quantity"},new Object[]{9999}, null));
        }

        //업무규칙 검증
        if(item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
//                errors.put("globalError", "가격 * 수량의 합이 10000이상이어야 합니다. 현재값 = " + resultPrice);
                bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"}, new Object[]{10000, resultPrice}, null));//글로벌 에러는 ObjectError 사용
            }
        }

        //검증이 실패하면 다시 입력폼으로 돌아가기
        if(bindingResult.hasErrors()){
            log.info("errors = {}", bindingResult);
//            model.addAttribute("errors", errors);bindingresult는 자동으로 viuw로 넘어가기 때문에 ModelAttribute에 넣을 필요가 없다.
            return "validation/v2/addForm";
        }

        //성공로직

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

