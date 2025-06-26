    import React, { useState, useEffect } from 'react';

    // Ícones simples para os botões (SVG inline)
    const EditIcon = () => (
      <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-1 inline" viewBox="0 0 20 20" fill="currentColor">
        <path d="M17.414 2.586a2 2 0 00-2.828 0L7 10.172V13h2.828l7.586-7.586a2 2 0 000-2.828z" />
        <path fillRule="evenodd" d="M2 6a2 2 0 012-2h4a1 1 0 010 2H4v10h10v-4a1 1 0 112 0v4a2 2 0 01-2 2H4a2 2 0 01-2-2V6z" clipRule="evenodd" />
      </svg>
    );

    const DeleteIcon = () => (
      <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-1 inline" viewBox="0 0 20 20" fill="currentColor">
        <path fillRule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clipRule="evenodd" />
      </svg>
    );

    const UserPlusIcon = () => (
      <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2 inline" viewBox="0 0 20 20" fill="currentColor">
        <path fillRule="evenodd" d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z" clipRule="evenodd" />
        <path d="M10 18a8 8 0 100-16 8 8 0 000 16zm0 2a10 10 0 100-20 10 10 0 000 20z" />
      </svg>
    );

    const CheckCircleIcon = () => (
      <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2 inline" viewBox="0 0 20 20" fill="currentColor">
        <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
      </svg>
    );

    const ExclamationCircleIcon = () => (
      <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2 inline" viewBox="0 0 20 20" fill="currentColor">
        <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
      </svg>
    );

    const SpinnerIcon = ({ className = "text-white" }) => (
      <svg className={`animate-spin -ml-1 mr-3 h-5 w-5 ${className} inline`} xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
      </svg>
    );

    // Dados iniciais mocados
    let DADOS_INICIAIS_USUARIOS = [ 
      { id: 1, firstname: 'Ana', lastname: 'Silva', email: 'ana.silva@email.com', ra: '11223344', cpf: '111.222.333-44', senha: 'senha123', tipo: 'Aluno', curso: 'Engenharia de Software', coordenador: false, perfis: [{ nome: 'ROLE_ALUNO' }] },
      { id: 2, firstname: 'Carlos', lastname: 'Pereira', email: 'carlos.pereira@email.com', ra: '99887766', cpf: '555.666.777-88', senha: 'outrasenha', tipo: 'Professor', curso: '', coordenador: true, perfis: [{ nome: 'ROLE_PROFESSOR' }, { nome: 'ROLE_COORDENADOR' }] },
    ];

    // Simulação de um serviço de API
    const apiService = {
      getUsuarios: async () => new Promise(resolve => setTimeout(() => resolve([...DADOS_INICIAIS_USUARIOS]), 800)),
      salvarUsuario: async (usuario) => new Promise(resolve => setTimeout(() => {
        if (Math.random() < 0.1) { 
            resolve({ success: false, message: "Falha simulada ao salvar no servidor." });
            return;
        }
        if (usuario.id) { 
          const index = DADOS_INICIAIS_USUARIOS.findIndex(u => u.id === usuario.id);
          if (index !== -1) {
            DADOS_INICIAIS_USUARIOS[index] = { ...DADOS_INICIAIS_USUARIOS[index], ...usuario };
            resolve({ success: true, data: { ...DADOS_INICIAIS_USUARIOS[index] } });
          } else { resolve({ success: false, message: "Utilizador não encontrado para atualizar."}); }
        } else { 
          const novoId = DADOS_INICIAIS_USUARIOS.length > 0 ? Math.max(...DADOS_INICIAIS_USUARIOS.map(u => u.id)) + 1 : 1;
          const novoUsuario = { ...usuario, id: novoId };
          DADOS_INICIAIS_USUARIOS.push(novoUsuario);
          resolve({ success: true, data: novoUsuario });
        }
      }, 1000)),
      excluirUsuario: async (id) => new Promise(resolve => setTimeout(() => {
        if (Math.random() < 0.1) { 
            resolve({ success: false, message: "Falha simulada ao excluir." });
            return;
        }
        const index = DADOS_INICIAIS_USUARIOS.findIndex(u => u.id === id);
        if (index !== -1) {
          DADOS_INICIAIS_USUARIOS = DADOS_INICIAIS_USUARIOS.filter(u => u.id !== id);
          resolve({ success: true });
        } else { resolve({ success: false, message: "Utilizador não encontrado." }); }
      }, 700))
    };

    // Componente de Notificação
    const Notification = ({ message, type, onClose }) => {
      if (!message) return null;
      const baseStyle = "p-4 rounded-md shadow-lg flex items-center text-sm";
      const typeStyles = {
        success: "bg-green-700 border-green-900 text-white",
        error: "bg-red-700 border-red-900 text-white",
      };
      return (
        <div className={`fixed top-5 right-5 z-[100] border ${baseStyle} ${typeStyles[type] || typeStyles.error}`}>
          {type === 'success' ? <CheckCircleIcon /> : <ExclamationCircleIcon />}
          <span className="ml-3">{message}</span>
          <button onClick={onClose} className="ml-auto -mx-1.5 -my-1.5 bg-transparent text-current p-1.5 rounded-lg focus:ring-2 focus:ring-offset-2 focus:ring-offset-current focus:ring-white inline-flex h-8 w-8" 
                  aria-label="Fechar">
            <span className="sr-only">Fechar</span>
            <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd"></path></svg>
          </button>
        </div>
      );
    };


    function App() {
      const [usuarios, setUsuarios] = useState([]);
      const [formularioAberto, setFormularioAberto] = useState(false);
      const [modoEdicao, setModoEdicao] = useState(false);
      const [termoBusca, setTermoBusca] = useState('');
      const [isLoading, setIsLoading] = useState(false);
      const [isSubmitting, setIsSubmitting] = useState(false);
      const [notification, setNotification] = useState({ message: '', type: '' });
      const [formErrors, setFormErrors] = useState({});

      const valorInicialFormulario = {
        id: null, tipo: 'Aluno', firstname: '', lastname: '', email: '', ra: '', cpf: '', senha: '',
        curso: '', coordenador: false,
      };
      const [dadosFormulario, setDadosFormulario] = useState(valorInicialFormulario);

      useEffect(() => {
        carregarUsuarios();
      }, []);

      const mostrarNotificacao = (message, type) => {
        setNotification({ message, type });
        setTimeout(() => setNotification({ message: '', type: '' }), 4000);
      };

      const carregarUsuarios = async () => {
        setIsLoading(true);
        try {
            const dados = await apiService.getUsuarios();
            setUsuarios(dados);
        } catch (error) {
            mostrarNotificacao("Erro ao carregar utilizadores.", "error");
        } finally {
            setIsLoading(false);
        }
      };

      const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setDadosFormulario(prevState => ({
          ...prevState,
          [name]: type === 'checkbox' ? checked : value,
        }));
        if (formErrors[name]) { 
            setFormErrors(prev => ({...prev, [name]: ''}));
        }
      };

      const validarFormulario = () => {
        const erros = {};
        if (!dadosFormulario.tipo) erros.tipo = "Tipo de utilizador é obrigatório.";
        if (!dadosFormulario.firstname.trim()) erros.firstname = "Primeiro nome é obrigatório.";
        if (!dadosFormulario.lastname.trim()) erros.lastname = "Sobrenome é obrigatório.";
        if (!dadosFormulario.email.trim()) erros.email = "Email é obrigatório.";
        else if (!/\S+@\S+\.\S+/.test(dadosFormulario.email)) erros.email = "Email inválido.";
        if (!dadosFormulario.ra.trim()) erros.ra = "RA é obrigatório.";
        if (!dadosFormulario.cpf.trim()) erros.cpf = "CPF é obrigatório.";
        if (!modoEdicao && !dadosFormulario.senha) erros.senha = "Senha é obrigatória para novos utilizadores.";
        else if (dadosFormulario.senha && dadosFormulario.senha.length < 6) erros.senha = "Senha deve ter no mínimo 6 caracteres.";
        
        setFormErrors(erros);
        return Object.keys(erros).length === 0; 
      };

      const handleAbrirFormularioNovo = () => {
        setModoEdicao(false);
        setDadosFormulario(valorInicialFormulario);
        setFormErrors({});
        setFormularioAberto(true);
      };

      const handleAbrirFormularioEditar = (usuario) => {
        setModoEdicao(true);
        setDadosFormulario({
            id: usuario.id, tipo: usuario.tipo, firstname: usuario.firstname, lastname: usuario.lastname,
            email: usuario.email, ra: usuario.ra, cpf: usuario.cpf, senha: '', 
            curso: usuario.tipo === 'Aluno' ? usuario.curso || '' : '', 
            coordenador: usuario.tipo === 'Professor' ? usuario.coordenador : false,
        });
        setFormErrors({});
        setFormularioAberto(true);
      };

      const handleFecharFormulario = () => {
        setFormularioAberto(false);
        setModoEdicao(false);
        setDadosFormulario(valorInicialFormulario); 
        setFormErrors({});
      };

      const handleSubmit = async (e) => {
        e.preventDefault();
        if (!validarFormulario()) return;

        setIsSubmitting(true);
        const dadosParaSalvar = { ...dadosFormulario };
        if (dadosFormulario.tipo === 'Professor') dadosParaSalvar.curso = ''; 
        else dadosParaSalvar.coordenador = false; 
        
        dadosParaSalvar.perfis = [];
        if (dadosParaSalvar.tipo === 'Aluno') dadosParaSalvar.perfis.push({ nome: 'ROLE_ALUNO' });
        else { 
            dadosParaSalvar.perfis.push({ nome: 'ROLE_PROFESSOR' });
            if (dadosParaSalvar.coordenador) dadosParaSalvar.perfis.push({ nome: 'ROLE_COORDENADOR' });
        }

        if (modoEdicao && dadosParaSalvar.senha === '') delete dadosParaSalvar.senha;

        const resposta = await apiService.salvarUsuario(dadosParaSalvar);
        setIsSubmitting(false);

        if (resposta.success) {
          const salvo = resposta.data;
          if (modoEdicao) setUsuarios(usuarios.map(u => (u.id === salvo.id ? salvo : u)));
          else setUsuarios([...usuarios, salvo]);
          mostrarNotificacao(`Utilizador ${modoEdicao ? 'atualizado' : 'cadastrado'} com sucesso!`, "success");
          handleFecharFormulario();
        } else {
            mostrarNotificacao(resposta.message || "Erro ao salvar utilizador.", "error");
        }
      };

      const [showConfirmDelete, setShowConfirmDelete] = useState(false);
      const [userToDelete, setUserToDelete] = useState(null);

      const abrirConfirmacaoExcluir = (id) => {
        setUserToDelete(id);
        setShowConfirmDelete(true);
      };

      const fecharConfirmacaoExcluir = () => {
        setUserToDelete(null);
        setShowConfirmDelete(false);
      };

      const handleExcluirConfirmado = async () => {
        if (!userToDelete) return;
        setIsSubmitting(true); 
        const resposta = await apiService.excluirUsuario(userToDelete);
        setIsSubmitting(false);
        fecharConfirmacaoExcluir();

        if (resposta.success) {
          setUsuarios(prevUsuarios => prevUsuarios.filter(u => u.id !== userToDelete));
          mostrarNotificacao("Utilizador excluído com sucesso!", "success");
        } else {
          mostrarNotificacao(resposta.message || "Erro ao excluir utilizador.", "error");
        }
      };


      const usuariosFiltrados = usuarios.filter(usuario =>
        `${usuario.firstname} ${usuario.lastname}`.toLowerCase().includes(termoBusca.toLowerCase()) ||
        usuario.email.toLowerCase().includes(termoBusca.toLowerCase()) ||
        (usuario.ra && usuario.ra.toLowerCase().includes(termoBusca.toLowerCase())) ||
        (usuario.cpf && usuario.cpf.toLowerCase().includes(termoBusca.toLowerCase()))
      );

      return (
        <div className="min-h-screen bg-slate-900 text-gray-200 p-4 sm:p-6 lg:p-8 font-sans">
          <Notification message={notification.message} type={notification.type} onClose={() => setNotification({ message: '', type: '' })} />
          
          <header className="mb-8">
            {/* Título principal alterado */}
            <h1 className="text-3xl sm:text-4xl font-bold text-[#d9025f] text-center">Gerenciar Usuários</h1> 
          </header>

          <div className="mb-6 flex flex-col sm:flex-row justify-between items-center gap-4">
            <input
              type="text"
              placeholder="Buscar utilizador (nome, email, RA, CPF)..."
              className="w-full sm:w-1/2 lg:w-1/3 px-4 py-2 border border-slate-700 bg-slate-800 text-gray-200 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-[#d9025f] placeholder-gray-500"
              value={termoBusca}
              onChange={(e) => setTermoBusca(e.target.value)}
            />
            <button
              onClick={handleAbrirFormularioNovo}
              className="w-full sm:w-auto bg-[#d9025f] hover:bg-[#c00253] text-white font-semibold py-2 px-4 rounded-lg shadow-md transition duration-150 ease-in-out flex items-center justify-center"
            >
              <UserPlusIcon /> Cadastrar Novo Utilizador
            </button>
          </div>

          {/* Modal de Confirmação de Exclusão */}
          {showConfirmDelete && (
            <div className="fixed inset-0 bg-black bg-opacity-75 overflow-y-auto h-full w-full z-[60] flex items-center justify-center p-4">
                <div className="bg-slate-800 p-6 rounded-lg shadow-xl w-full max-w-md border border-slate-700">
                    <h3 className="text-lg font-medium leading-6 text-[#d9025f] mb-2">Confirmar Exclusão</h3>
                    <p className="text-sm text-gray-400 mb-4">
                        Tem certeza que deseja excluir este utilizador? Esta ação não pode ser desfeita.
                    </p>
                    <div className="flex justify-end space-x-3">
                        <button
                            type="button"
                            onClick={fecharConfirmacaoExcluir}
                            disabled={isSubmitting}
                            className="px-4 py-2 text-sm font-medium text-gray-300 bg-slate-700 hover:bg-slate-600 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-slate-800 focus:ring-[#d9025f] transition duration-150"
                        >
                            Cancelar
                        </button>
                        <button
                            type="button"
                            onClick={handleExcluirConfirmado}
                            disabled={isSubmitting}
                            className="px-4 py-2 text-sm font-medium text-white bg-red-600 hover:bg-red-700 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-slate-800 focus:ring-red-500 transition duration-150 flex items-center"
                        >
                            {isSubmitting ? <SpinnerIcon className="text-white"/> : <DeleteIcon />} Excluir
                        </button>
                    </div>
                </div>
            </div>
          )}

          {/* Formulário Modal */}
          {formularioAberto && (
            <div className="fixed inset-0 bg-black bg-opacity-75 overflow-y-auto h-full w-full z-50 flex items-center justify-center p-4">
              <div className="bg-slate-800 p-6 sm:p-8 rounded-lg shadow-xl w-full max-w-lg max-h-[90vh] overflow-y-auto border border-slate-700">
                <h2 className="text-2xl font-semibold text-[#d9025f] mb-6">{modoEdicao ? 'Editar Utilizador' : 'Cadastrar Novo Utilizador'}</h2>
                <form onSubmit={handleSubmit} className="space-y-3">
                  <div>
                    <label htmlFor="tipo" className="block text-sm font-medium text-gray-300">Tipo de Utilizador</label>
                    <select name="tipo" id="tipo" value={dadosFormulario.tipo} onChange={handleChange}
                            className={`mt-1 block w-full px-3 py-2 border bg-slate-700 text-gray-200 rounded-md shadow-sm focus:outline-none focus:ring-[#d9025f] focus:border-[#d9025f] sm:text-sm ${formErrors.tipo ? 'border-red-500' : 'border-slate-600'}`}>
                      <option value="Aluno">Aluno</option>
                      <option value="Professor">Professor</option>
                    </select>
                    {formErrors.tipo && <p className="mt-1 text-xs text-red-400">{formErrors.tipo}</p>}
                  </div>

                  <div>
                    <label htmlFor="firstname" className="block text-sm font-medium text-gray-300">Primeiro Nome</label>
                    <input type="text" name="firstname" id="firstname" value={dadosFormulario.firstname} onChange={handleChange} required 
                           className={`mt-1 block w-full px-3 py-2 border bg-slate-700 text-gray-200 rounded-md shadow-sm focus:outline-none focus:ring-[#d9025f] focus:border-[#d9025f] sm:text-sm placeholder-gray-500 ${formErrors.firstname ? 'border-red-500' : 'border-slate-600'}`}/>
                    {formErrors.firstname && <p className="mt-1 text-xs text-red-400">{formErrors.firstname}</p>}
                  </div>
                  <div>
                    <label htmlFor="lastname" className="block text-sm font-medium text-gray-300">Sobrenome</label>
                    <input type="text" name="lastname" id="lastname" value={dadosFormulario.lastname} onChange={handleChange} required
                           className={`mt-1 block w-full px-3 py-2 border bg-slate-700 text-gray-200 rounded-md shadow-sm focus:outline-none focus:ring-[#d9025f] focus:border-[#d9025f] sm:text-sm placeholder-gray-500 ${formErrors.lastname ? 'border-red-500' : 'border-slate-600'}`}/>
                    {formErrors.lastname && <p className="mt-1 text-xs text-red-400">{formErrors.lastname}</p>}
                  </div>
                  <div>
                    <label htmlFor="email" className="block text-sm font-medium text-gray-300">Email</label>
                    <input type="email" name="email" id="email" value={dadosFormulario.email} onChange={handleChange} required
                           className={`mt-1 block w-full px-3 py-2 border bg-slate-700 text-gray-200 rounded-md shadow-sm focus:outline-none focus:ring-[#d9025f] focus:border-[#d9025f] sm:text-sm placeholder-gray-500 ${formErrors.email ? 'border-red-500' : 'border-slate-600'}`}/>
                    {formErrors.email && <p className="mt-1 text-xs text-red-400">{formErrors.email}</p>}
                  </div>
                  <div>
                    <label htmlFor="ra" className="block text-sm font-medium text-gray-300">RA (Registo Académico)</label>
                    <input type="text" name="ra" id="ra" value={dadosFormulario.ra} onChange={handleChange} required
                           className={`mt-1 block w-full px-3 py-2 border bg-slate-700 text-gray-200 rounded-md shadow-sm focus:outline-none focus:ring-[#d9025f] focus:border-[#d9025f] sm:text-sm placeholder-gray-500 ${formErrors.ra ? 'border-red-500' : 'border-slate-600'}`}/>
                    {formErrors.ra && <p className="mt-1 text-xs text-red-400">{formErrors.ra}</p>}
                  </div>
                  <div>
                    <label htmlFor="cpf" className="block text-sm font-medium text-gray-300">CPF</label>
                    <input type="text" name="cpf" id="cpf" value={dadosFormulario.cpf} onChange={handleChange} required
                           className={`mt-1 block w-full px-3 py-2 border bg-slate-700 text-gray-200 rounded-md shadow-sm focus:outline-none focus:ring-[#d9025f] focus:border-[#d9025f] sm:text-sm placeholder-gray-500 ${formErrors.cpf ? 'border-red-500' : 'border-slate-600'}`}/>
                    {formErrors.cpf && <p className="mt-1 text-xs text-red-400">{formErrors.cpf}</p>}
                  </div>
                  <div>
                    <label htmlFor="senha" className="block text-sm font-medium text-gray-300">Senha {modoEdicao && "(Deixe em branco para não alterar)"}</label>
                    <input type="password" name="senha" id="senha" value={dadosFormulario.senha} onChange={handleChange} 
                           required={!modoEdicao} 
                           className={`mt-1 block w-full px-3 py-2 border bg-slate-700 text-gray-200 rounded-md shadow-sm focus:outline-none focus:ring-[#d9025f] focus:border-[#d9025f] sm:text-sm placeholder-gray-500 ${formErrors.senha ? 'border-red-500' : 'border-slate-600'}`}/>
                    {formErrors.senha && <p className="mt-1 text-xs text-red-400">{formErrors.senha}</p>}
                    </div>
                  
                  {dadosFormulario.tipo === 'Professor' && (
                    <div className="flex items-center mt-2">
                      <input type="checkbox" name="coordenador" id="coordenador" checked={dadosFormulario.coordenador} onChange={handleChange}
                             className="h-4 w-4 text-[#d9025f] border-slate-600 rounded focus:ring-[#d9025f] bg-slate-700"/>
                      <label htmlFor="coordenador" className="ml-2 block text-sm text-gray-300">É Coordenador?</label>
                    </div>
                  )}

                  <div className="flex items-center justify-end space-x-3 pt-4">
                    <button type="button" onClick={handleFecharFormulario} disabled={isSubmitting}
                            className="px-4 py-2 text-sm font-medium text-gray-300 bg-slate-700 hover:bg-slate-600 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-slate-800 focus:ring-[#d9025f] transition duration-150 disabled:opacity-50">
                      Cancelar
                    </button>
                    <button type="submit" disabled={isSubmitting}
                            className="px-4 py-2 text-sm font-medium text-white bg-[#d9025f] hover:bg-[#c00253] rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-slate-800 focus:ring-[#d9025f] transition duration-150 flex items-center justify-center disabled:opacity-50">
                      {isSubmitting && <SpinnerIcon className="text-white"/>}
                      {isSubmitting ? (modoEdicao ? 'Salvando...' : 'Cadastrando...') : (modoEdicao ? 'Salvar Alterações' : 'Cadastrar Utilizador')}
                    </button>
                  </div>
                </form>
              </div>
            </div>
          )}

          {/* Tabela de Utilizadores */}
          <div className="bg-slate-800 shadow-lg rounded-lg overflow-x-auto border border-slate-700">
            <table className="min-w-full divide-y divide-slate-700">
              <thead className="bg-slate-700/50">
                <tr>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-white uppercase tracking-wider">Nome Completo</th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-white uppercase tracking-wider">Email</th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-white uppercase tracking-wider">RA</th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-white uppercase tracking-wider">CPF</th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-white uppercase tracking-wider">Tipo</th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-white uppercase tracking-wider">Detalhes</th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-white uppercase tracking-wider">Ações</th>
                </tr>
              </thead>
              <tbody className="bg-slate-800 divide-y divide-slate-700">
                {isLoading ? (
                    <tr><td colSpan="7" className="text-center py-10 text-gray-400"><SpinnerIcon className="text-[#d9025f]"/> Carregando utilizadores...</td></tr>
                ) : usuariosFiltrados.length > 0 ? usuariosFiltrados.map((usuario) => (
                  <tr key={usuario.id} className="hover:bg-slate-700/50 transition duration-150">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-gray-100">{usuario.firstname} {usuario.lastname}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-300">{usuario.email}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-300">{usuario.ra}</div>
                    </td>
                     <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-300">{usuario.cpf}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                        usuario.tipo === 'Aluno' ? 'bg-green-700 text-green-100' : 'bg-purple-700 text-purple-100'
                      }`}>
                        {usuario.tipo}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-300">
                      {usuario.tipo === 'Aluno' ? `Curso: ${usuario.curso || 'N/A'}` : (usuario.coordenador ? 'Coordenador' : 'Professor')}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2">
                      <button onClick={() => handleAbrirFormularioEditar(usuario)} className="text-white hover:text-gray-200 transition duration-150 p-1 rounded hover:bg-slate-700 disabled:opacity-50" disabled={isSubmitting}>
                        <EditIcon /> Editar
                      </button>
                      <button onClick={() => abrirConfirmacaoExcluir(usuario.id)} className="text-red-400 hover:text-red-300 transition duration-150 p-1 rounded hover:bg-slate-700 disabled:opacity-50" disabled={isSubmitting}>
                        <DeleteIcon /> Excluir
                      </button>
                    </td>
                  </tr>
                )) : (
                  <tr>
                    <td colSpan="7" className="px-6 py-12 text-center text-sm text-gray-500">
                      Nenhum utilizador encontrado com os critérios de busca ou nenhum utilizador cadastrado.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
           <footer className="mt-12 text-center text-sm text-gray-500">
            <p>&copy; {new Date().getFullYear()} Sistema Académico Minimalista. Todos os direitos reservados.</p>
          </footer>
        </div>
      );
    }

    export default App;

    