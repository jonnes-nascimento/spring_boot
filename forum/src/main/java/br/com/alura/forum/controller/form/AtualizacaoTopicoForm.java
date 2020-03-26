package br.com.alura.forum.controller.form;

import java.util.Optional;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.alura.forum.model.Topico;

// as classes "FORM" sao usadas para receber dados dos clientes
public class AtualizacaoTopicoForm {
	
	@NotNull @NotEmpty @Size(min = 5)
	public String titulo;
	
	@NotNull @NotEmpty @Size(min = 10)
	public String mensagem;

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public Topico atualizar(Long id, Optional<Topico> topicoOpt) {
		
		Topico topico = topicoOpt.get();
		
		topico.setTitulo(this.titulo);
		topico.setMensagem(this.mensagem);
		
		return topico;
	}
	
}
