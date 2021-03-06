/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufrn.imd.web2.keepit.view;

import br.ufrn.imd.web2.keepit.data.DespesaIncomumLocalDAO;
import br.ufrn.imd.web2.keepit.entity.DespesaIncomum;
import br.ufrn.imd.web2.keepit.exception.BusinessException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author franklin
 */
@Named(value = "controladorDespesaIncomum")
@RequestScoped
public class ControladorDespesaIncomum {

    private DespesaIncomum despesaIncomum;

    @EJB(beanName = "despesaIncomumDAO", beanInterface = DespesaIncomumLocalDAO.class)
    private DespesaIncomumLocalDAO despesaIncomumDAO;

    @Inject
    private ControladorLogin controladorLogin;

    @Inject
    private ControladorUsuario controladorUsuario;

    public DespesaIncomum getDespesaIncomum() {
        return despesaIncomum;
    }

    public void setDespesaIncomum(DespesaIncomum despesaIncomum) {
        this.despesaIncomum = despesaIncomum;
    }

    public void criarDespesaIncomum() {
        this.despesaIncomum.setUsuario(controladorLogin.getUsuario());
        try {
            if (this.controladorLogin.getUsuario().getSaldo() >= this.despesaIncomum.getValor()) {
                this.despesaIncomumDAO.create(despesaIncomum);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Despesa incomum adicionada!", "Sucesso!"));
                this.controladorLogin.getUsuario().setSaldo(this.controladorLogin.getUsuario().getSaldo() - this.despesaIncomum.getValor());
                this.controladorUsuario.editarUsuario(this.controladorLogin.getUsuario());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Saldo atualizado!", "Sucesso!"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Saldo insuficiente", "Falha!"));
            }
        } catch (BusinessException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "Falha!"));
        }
        this.initObject();
    }

    public void removerDespesaIncomum(DespesaIncomum despesaIncomum) {
        this.despesaIncomumDAO.remove(despesaIncomum);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Despesa " + despesaIncomum.getTitulo() + " foi removida!", "Sucesso!"));
    }

    public void removerDesfazerAlteracoes(DespesaIncomum despesaIncomum) {
        this.despesaIncomumDAO.remove(despesaIncomum);
        this.controladorLogin.getUsuario().setSaldo(this.controladorLogin.getUsuario().getSaldo() + despesaIncomum.getValor());
        this.controladorUsuario.editarUsuario(this.controladorLogin.getUsuario());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Despesa " + despesaIncomum.getTitulo() + " foi removida e seu saldo foi atualizado!", "Sucesso!"));
    }

    public void atualizarDespesaIncomum() {
        this.despesaIncomumDAO.edit(despesaIncomum);
    }

    public List<DespesaIncomum> getDespesasIncomuns() {
        return despesaIncomumDAO.findByLoggedUser(controladorLogin.getUsuario().getId());
    }

    @PostConstruct
    private void initObject() {
        this.despesaIncomum = new DespesaIncomum();
    }
}
