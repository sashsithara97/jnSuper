package J_N_Super_Pvt_Ltd.asset.PurchaseOrder.controller;


import J_N_Super_Pvt_Ltd.asset.PurchaseOrder.entity.Enum.PurchaseOrderStatus;
import J_N_Super_Pvt_Ltd.asset.PurchaseOrder.entity.PurchaseOrder;
import J_N_Super_Pvt_Ltd.asset.PurchaseOrder.service.PurchaseOrderItemService;
import J_N_Super_Pvt_Ltd.asset.PurchaseOrder.service.PurchaseOrderService;
import J_N_Super_Pvt_Ltd.asset.commonAsset.service.CommonService;
import J_N_Super_Pvt_Ltd.asset.employee.controller.EmployeeRestController;
import J_N_Super_Pvt_Ltd.asset.employee.entity.Employee;
import J_N_Super_Pvt_Ltd.asset.employee.entity.Enum.Designation;
import J_N_Super_Pvt_Ltd.asset.supplier.entity.Supplier;
import J_N_Super_Pvt_Ltd.asset.supplier.service.SupplierItemService;
import J_N_Super_Pvt_Ltd.asset.supplier.service.SupplierService;
import J_N_Super_Pvt_Ltd.asset.userManagement.service.RoleService;
import J_N_Super_Pvt_Ltd.asset.userManagement.service.UserService;
import J_N_Super_Pvt_Ltd.util.service.EmailService;
import J_N_Super_Pvt_Ltd.util.service.MakeAutoGenerateNumberService;
import J_N_Super_Pvt_Ltd.util.service.OperatorService;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.HashBiMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.HashSet;

@Controller
@RequestMapping("/purchaseOrder")
public class PurchaseOrderController {
    private final PurchaseOrderService purchaseOrderService;
    private final PurchaseOrderItemService purchaseOrderItemService;
    private final SupplierService supplierService;
    private final CommonService commonService;
    private final MakeAutoGenerateNumberService makeAutoGenerateNumberService;
    private final EmailService emailService;
    private final OperatorService operatorService;
    private final SupplierItemService supplierItemService;

    public PurchaseOrderController(PurchaseOrderService supplierItemService, PurchaseOrderService purchaseOrderService, PurchaseOrderItemService purchaseOrderItemService, SupplierService supplierService, CommonService commonService, MakeAutoGenerateNumberService makeAutoGenerateNumberService, EmailService emailService, OperatorService operatorService, SupplierItemService supplierItemService1) {
        this.purchaseOrderService = purchaseOrderService;
        this.purchaseOrderItemService = purchaseOrderItemService;
        this.supplierService = supplierService;
        this.commonService = commonService;
        this.makeAutoGenerateNumberService = makeAutoGenerateNumberService;
        this.emailService = emailService;
        this.operatorService = operatorService;
        this.supplierItemService = supplierItemService1;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("purchaseOrder", purchaseOrderService.findAll());
        model.addAttribute("searchAreaShow", true);
        return "purchaseOrder/purchaseOrder";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("purchaseOrder", new Supplier());
        model.addAttribute("searchAreaShow", true);
        return "purchaseOrder/addPurchaseOrder";
    }

    @PostMapping("/find")
    public String search(@Valid @ModelAttribute Supplier supplier, Model model) {
        return commonService.purchaseOrder(supplier, model, "purchaseOrder/addPurchaseOrder");
    }


    @GetMapping("/{id}")
    public String view(@PathVariable Integer id, Model model) {
        return commonService.purchaseOrder(model, id);

    }

    @PostMapping
    public String purchaseOrderPersist(@Valid @ModelAttribute PurchaseOrder purchaseOrder, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "redirect:/purchaseOrder/" + purchaseOrder.getId();
        }
       /* purchaseOrder.getPurchaseOrderSuppliers()
                .forEach(purchaseOrderSupplier -> {
                    purchaseOrderSupplier.setPurchaseOrder(purchaseOrder);
                    //todo need to create batch number
                });*/
        purchaseOrder.setPurchaseOrderStatus(PurchaseOrderStatus.NOT_COMPLETED);
        if (purchaseOrder.getId() != null) {
            if (purchaseOrderService.lastPurchaseOrder() == null) {
                //need to generate new one
                purchaseOrder.setCode("JNPO" + makeAutoGenerateNumberService.numberAutoGen(null).toString());
            } else {
                System.out.println("last customer not null");
                //if there is customer in db need to get that customer's code and increase its value
                String previousCode = purchaseOrderService.lastPurchaseOrder().getCode().substring(3);
                purchaseOrder.setCode("JNPO" + makeAutoGenerateNumberService.numberAutoGen(previousCode).toString());
            }
        }
        PurchaseOrder purchaseOrderSaved = purchaseOrderService.persist(purchaseOrder);
/*        if (purchaseOrderSaved.getSupplier().getEmail() != null) {
            StringBuilder message = new StringBuilder("Item Name\t\t\t\t\tQuantity\t\t\tItem Price\t\t\tTotal(Rs)\n");
            for (int i = 0; i < purchaseOrder.getPurchaseOrderItems().size(); i++) {
                message
                        .append(purchaseOrder.getPurchaseOrderItems().get(i).getItem().getName())
                        .append("\t\t\t\t\t")
                        .append(purchaseOrderSaved.getPurchaseOrderItems().get(i).getQuantity())
                        .append("\t\t\t")
                        .append(purchaseOrderSaved.getPurchaseOrderItems().get(i).getPrice()).append("\t\t\t")
                        .append(operatorService.multiply(
                                purchaseOrderSaved.getPurchaseOrderItems().get(i).getPrice(),
                                new BigDecimal(Integer.parseInt(purchaseOrderSaved.getPurchaseOrderItems().get(i).getQuantity()))
                        ))
                        .append("\n");
            }
            emailService.sendEmail(purchaseOrderSaved.getSupplier().getEmail(), "Requesting Items According To PO Code " + purchaseOrder.getCode(), message.toString());
        }*/
        return "redirect:/purchaseOrder/all";
    }

    @GetMapping("/all")
    public String findAll(Model model) {
        model.addAttribute("purchaseOrders", purchaseOrderService.findAll());
        return "purchaseOrder/purchaseOrder";
    }

    @GetMapping("view/{id}")
    public String viewPurchaseOrderDetail(@PathVariable Integer id, Model model) {
        model.addAttribute("purchaseOrder-details", purchaseOrderService.findById(id));
        return "purchaseOrder/purchaseOrder-detail";
    }

    @GetMapping("delete/{id}")
    public String deletePurchaseOrderDetail(@PathVariable Integer id) {
        PurchaseOrder purchaseOrder = purchaseOrderService.findById(id);
        purchaseOrder.setPurchaseOrderStatus(PurchaseOrderStatus.NOT_PROCEED);
        purchaseOrderService.persist(purchaseOrder);
        //model.addAttribute("purchaseOrder-details", purchaseOrderService.findById(id));
        return "redirect:/purchaseOrder/all";
    }

    //todo -> need to  manage item price displaying option and amount calculation
    @GetMapping("/supplier/{id}")
    public String addPriceToSupplierItem(@PathVariable int id, Model model) {
        Supplier supplier = supplierService.findById(id);
        // supplier.setSupplierItems(purchaseOrders);
        model.addAttribute("supplierDetail", supplier);
        model.addAttribute("supplierDetailShow", false);
        model.addAttribute("purchaseOrderItemEdit", false);
        //send all active item belongs to supplier
        model.addAttribute("items", commonService.activeItemsFromSupplier(supplier));
        Object[] argument = {"", ""};
        model.addAttribute("purchaseOrderItemUrl", MvcUriComponentsBuilder
                .fromMethodName(PurchaseOrderController.class, "purchaseOrderSupplierItem", argument)
                .build()
                .toString());

        return "purchaseOrder/addPurchaseOrder";
    }

    @GetMapping(value = "/supplierItem", params = {"supplierId", "itemId"})
    @ResponseBody
    public Object purchaseOrderSupplierItem(@RequestParam("supplierId") Integer supplierId, @RequestParam("itemId") Integer itemId) {
        //  MappingJacksonValue

        System.out.println("supplier id " + supplierId + "      item id " + itemId);
        HashMap<String,String> purchaseOrderItem = new HashMap<>();


        /*
         * 1. ID
         * 2. Item name
         * 3. Rop
         * 4. Price
         * 5. Available Quantity.
         * */

        //MappingJacksonValue
        List<Employee> employees = employeeService.search(employee);
        //employeeService.findByWorkingPlace(workingPlaceService.findById(id));

        //Create new mapping jackson value and set it to which was need to filter
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(employees);

        //simpleBeanPropertyFilter :-  need to give any id to addFilter method and created filter which was mentioned
        // what parameter's necessary to provide
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter
                .filterOutAllExcept("id", "name", "payRoleNumber", "designation");
        //filters :-  set front end required value to before filter

        FilterProvider filters = new SimpleFilterProvider()
                .addFilter("Employee", simpleBeanPropertyFilter);
        //Employee :- need to annotate relevant class with JsonFilter  {@JsonFilter("Employee") }
        mappingJacksonValue.setFilters(filters);

        //return mappingJacksonValue;

    }

}

