package com.example.aula.controller;

import com.example.aula.model.entity.*;
import com.example.aula.model.repository.PessoaFisicaRepository;
import com.example.aula.model.repository.PessoaJuridicaRepository;
import com.example.aula.model.repository.ProdutoRepository;
import com.example.aula.model.repository.VendaRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.List;

@Transactional
@Scope("request")
@Controller
@RequestMapping("/vendas")
public class VendaController {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    Venda venda; //O spring vai criar o objeto na session

    @Autowired
    private ProdutoRepository produtoRepository;


    @Autowired
    PessoaFisicaRepository pessoaFisicaRepository;

    @Autowired
    PessoaJuridicaRepository pessoaJuridicaRepository;


    @GetMapping("produto/add/{id}")
    public ModelAndView produtoAdd(@PathVariable("id") Long produtoId) {
        Produto produto = produtoRepository.produto(produtoId);

        //Verifica se o produto retornado do repositório não é nulo antes de continuar.
        if (produto != null) {
            ItemVenda itemExistente = null;

            //apenas percorre a lista de itens de venda para encontrar o item existente.
            for (ItemVenda itemVenda : venda.getItemVendas()) {
                if (itemVenda.getProduto().getId().equals(produtoId)) {
                    itemExistente = itemVenda;
                    break;
                }
            }

            //Usa uma variável itemExistente para armazenar o item encontrado e atualiza
            //sua quantidade, ou adiciona um novo item de venda se não for encontrado.
            if (itemExistente != null) {
                itemExistente.setQuantidade(itemExistente.getQuantidade() + 1);
            } else {
                ItemVenda itemNovo = new ItemVenda();
                itemNovo.setProduto(produto);
                itemNovo.setQuantidade(1);
                itemNovo.setVenda(venda);
                venda.getItemVendas().add(itemNovo);
            }
        }

        return new ModelAndView("redirect:/produtos/area-compra");
    }

@PostMapping("/save")
public ModelAndView save(@Valid @ModelAttribute("venda") Venda venda,
                         BindingResult result,
                         @RequestParam("pessoaId") Long id,
                         HttpSession session) {


    // Verifique se o carrinho está vazio
    if (venda.getItemVendas().isEmpty()) {
        return new ModelAndView("redirect:/produtos/area-compra");
    }

    // Processamento normal
    Pessoa pessoa = buscarPessoa(id);
    venda.setPessoa(pessoa);
    venda.setDataHora(LocalDate.now());
    vendaRepository.save(venda);
    session.invalidate();

    return new ModelAndView("redirect:/vendas/list");
}


    // - VENDAS/CARRINHO
    @GetMapping("/carrinho")
    public ModelAndView carrinholista(ModelMap model){
        model.addAttribute("pf", pessoaFisicaRepository.pessoaFisica());
        model.addAttribute("pj", pessoaJuridicaRepository.pessoaJuridica());
        model.addAttribute("itens", vendaRepository.vendas());
        return new ModelAndView();
    }


    @GetMapping(path = {"", "/", "list"})
    public ModelAndView listar(ModelMap model){
        model.addAttribute("vendas", vendaRepository.vendas());
        return new ModelAndView("/vendas/list", model);
    }

    @GetMapping("/{id}")
    public ModelAndView listar(@PathVariable("id") String id, ModelMap model){
        try {
            Venda venda = vendaRepository.venda(Long.parseLong(id));
            if (venda == null) throw new RuntimeException("Object(Venda) is null");

            model.addAttribute("venda", venda);
            return new ModelAndView("/vendas/view", model);
        } catch (Exception e) {
            model.clear();
            return new ModelAndView("/vendas/list", model);
        }
    }

    @GetMapping("/session/remove/{index}")
    public ModelAndView removeFromSession(@PathVariable("index") int index) {
        List<ItemVenda> itemVendas = this.venda.getItemVendas();
        ItemVenda item = itemVendas.get(index);

        if (item.getQuantidade() > 1) {
            item.setQuantidade(item.getQuantidade() - 1);
        } else {
            this.venda.getItemVendas().remove(index);
        }

        return new ModelAndView("redirect:/vendas/carrinho");
    }


    private Pessoa buscarPessoa(Long id){
        PessoaFisica pessoaFisica = pessoaFisicaRepository.pessoaFisica(id);
        if (pessoaFisica != null){
            return pessoaFisica;
        }

        PessoaJuridica pessoaJuridica = pessoaJuridicaRepository.pessoaJuridica(id);

        if (pessoaJuridica != null){
            return pessoaJuridica;
        }
        return null;
    }
}
