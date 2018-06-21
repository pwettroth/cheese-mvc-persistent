package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Menus");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model) {

        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());

        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddMenuForm(@Valid Menu newMenu, Errors errors, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(newMenu);

        return "redirect:view/" + newMenu.getId();
    }

    @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int id) {

        model.addAttribute("title", "View Menu");
        model.addAttribute("menu", menuDao.findOne(id));

        return "menu/view";
    }

    @RequestMapping(value = "add-item/{id}", method = RequestMethod.GET)
    public String displayAddItem(Model model, @PathVariable int id) {

        Menu theMenu = menuDao.findOne(id);

        AddMenuItemForm form = new AddMenuItemForm();
        form.setMenu(theMenu);
        form.setCheeses(theMenu.getCheeses());

        model.addAttribute("form", form);
        model.addAttribute("title", "Add item to menu:" + form.getMenuId());

        model.addAttribute("cheeses", cheeseDao.findAll());
        return "menu/add-item";
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String processAddItem(@Valid AddMenuItemForm newForm, Model model, Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu Item");
            return "menu/add-item";
        }

        Cheese theCheese = cheeseDao.findOne(newForm.getCheeseId());
        Menu theMenu = menuDao.findOne(newForm.getMenuId());

        theMenu.addItem(theCheese);
        menuDao.save(theMenu);

        return "redirect:view/" + theMenu.getId();
    }

}
