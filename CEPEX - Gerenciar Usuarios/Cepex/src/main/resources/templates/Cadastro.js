import React, { useState }
    from 'react';

// Componente para o formulário de Aluno
const AlunoForm = () => {
    const [formData, setFormData] = useState({
        raAluno: '',
        cpfAluno: '',
        primeiroNomeAluno: '',
        sobrenomeAluno: '',
        emailAluno: '',
        senhaAluno: '',
        confirmarSenhaAluno: '',
    });
    const [erroSenha, setErroSenha] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (formData.senhaAluno !== formData.confirmarSenhaAluno) {
            setErroSenha(true);
            return;
        }
        setErroSenha(false);
        console.log("Dados do Aluno:", formData);
        alert("Cadastro de Aluno simulado! Verifique o console para os dados.");
        // Reset form (opcional, pode ser melhor limpar os campos individualmente)
        setFormData({
            raAluno: '',
            cpfAluno: '',
            primeiroNomeAluno: '',
            sobrenomeAluno: '',
            emailAluno: '',
            senhaAluno: '',
            confirmarSenhaAluno: '',
        });
    };

    return (
        <section id="formAluno" className="form-section active">
            <h2 className="text-2xl font-semibold mb-6 text-sky-600">Informações do Aluno</h2>
            <form onSubmit={handleSubmit}>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-4">
                    <div>
                        <label htmlFor="raAluno" className="block text-sm font-medium text-gray-700 mb-1">RA (Registro Acadêmico)</label>
                        <input
                            type="text"
                            id="raAluno"
                            name="raAluno"
                            required
                            placeholder="Ex: 123456"
                            className="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring-sky-500 focus:border-sky-500 transition duration-150"
                            value={formData.raAluno}
                            onChange={handleChange}
                        />
                    </div>
                    <div>
                        <label htmlFor="cpfAluno" className="block text-sm font-medium text-gray-700 mb-1">CPF</label>
                        <input
                            type="text"
                            id="cpfAluno"
                            name="cpfAluno"
                            required
                            placeholder="000.000.000-00"
                            className="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring-sky-500 focus:border-sky-500 transition duration-150"
                            value={formData.cpfAluno}
                            onChange={handleChange}
                        />
                    </div>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-4">
                    <div>
                        <label htmlFor="primeiroNomeAluno" className="block text-sm font-medium text-gray-700 mb-1">Primeiro Nome</label>
                        <input
                            type="text"
                            id="primeiroNomeAluno"
                            name="primeiroNomeAluno"
                            required
                            className="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring-sky-500 focus:border-sky-500 transition duration-150"
                            value={formData.primeiroNomeAluno}
                            onChange={handleChange}
                        />
                    </div>
                    <div>
                        <label htmlFor="sobrenomeAluno" className="block text-sm font-medium text-gray-700 mb-1">Sobrenome</label>
                        <input
                            type="text"
                            id="sobrenomeAluno"
                            name="sobrenomeAluno"
                            required
                            className="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring-sky-500 focus:border-sky-500 transition duration-150"
                            value={formData.sobrenomeAluno}
                            onChange={handleChange}
                        />
                    </div>
                </div>
                <div className="mb-4">
                    <label htmlFor="emailAluno" className="block text-sm font-medium text-gray-700 mb-1">E-mail</label>
                    <input
                        type="email"
                        id="emailAluno"
                        name="emailAluno"
                        required
                        placeholder="seuemail@dominio.com"
                        className="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring-sky-500 focus:border-sky-500 transition duration-150"
                        value={formData.emailAluno}
                        onChange={handleChange}
                    />
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                    <div>
                        <label htmlFor="senhaAluno" className="block text-sm font-medium text-gray-700 mb-1">Senha</label>
                        <input
                            type="password"
                            id="senhaAluno"
                            name="senhaAluno"
                            required
                            className="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring-sky-500 focus:border-sky-500 transition duration-150"
                            value={formData.senhaAluno}
                            onChange={handleChange}
                        />
                    </div>
                    <div>
                        <label htmlFor="confirmarSenhaAluno" className="block text-sm font-medium text-gray-700 mb-1">Confirmar Senha</label>
                        <input
                            type="password"
                            id="confirmarSenhaAluno"
                            name="confirmarSenhaAluno"
                            required
                            className="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring-sky-500 focus:border-sky-500 transition duration-150"
                            value={formData.confirmarSenhaAluno}
                            onChange={handleChange}
                        />
                    </div>
                </div>
                {erroSenha && <div id="erroSenhaAluno" className="text-red-600 text-sm mb-4">As senhas não conferem.</div>}
                <div className="flex justify-end">
                    <button type="submit" className="bg-sky-600 hover:bg-sky-700 text-white font-semibold py-3 px-6 rounded-md shadow-md hover:shadow-lg focus:outline-none focus:ring-2 focus:ring-sky-500 focus:ring-opacity-50 transition duration-150">
                        Cadastrar Aluno
                    </button>
                </div>
            </form>
        </section>
    );
};

// Componente para o formulário de Professor
const ProfessorForm = () => {
    const [formData, setFormData] = useState({
        raProfessor: '',
        cpfProfessor: '',
        primeiroNomeProfessor: '',
        sobrenomeProfessor: '',
        emailProfessor: '',
        senhaProfessor: '',
        confirmarSenhaProfessor: '',
        ehCoordenador: false,
    });
    const [erroSenha, setErroSenha] = useState(false);

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (formData.senhaProfessor !== formData.confirmarSenhaProfessor) {
            setErroSenha(true);
            return;
        }
        setErroSenha(false);
        console.log("Dados do Professor:", formData);
        alert("Cadastro de Professor simulado! Verifique o console para os dados.");
        // Reset form
        setFormData({
            raProfessor: '',
            cpfProfessor: '',
            primeiroNomeProfessor: '',
            sobrenomeProfessor: '',
            emailProfessor: '',
            senhaProfessor: '',
            confirmarSenhaProfessor: '',
            ehCoordenador: false,
        });
    };

    return (
        <section id="formProfessor" className="form-section active">
            <h2 className="text-2xl font-semibold mb-6 text-sky-600">Informações do Professor</h2>
            <form onSubmit={handleSubmit}>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-4">
                    <div>
                        <label htmlFor="raProfessor" className="block text-sm font-medium text-gray-700 mb-1">RA / Identificador Funcional</label>
                        <input
                            type="text"
                            id="raProfessor"
                            name="raProfessor"
                            required
                            placeholder="Ex: PROF98765"
                            className="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring-sky-500 focus:border-sky-500 transition duration-150"
                            value={formData.raProfessor}
                            onChange={handleChange}
                        />
                    </div>
                    <div>
                        <label htmlFor="cpfProfessor" className="block text-sm font-medium text-gray-700 mb-1">CPF</label>
                        <input
                            type="text"
                            id="cpfProfessor"
                            name="cpfProfessor"
                            required
                            placeholder="000.000.000-00"
                            className="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring-sky-500 focus:border-sky-500 transition duration-150"
                            value={formData.cpfProfessor}
                            onChange={handleChange}
                        />
                    </div>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-4">
                    <div>
                        <label htmlFor="primeiroNomeProfessor" className="block text-sm font-medium text-gray-700 mb-1">Primeiro Nome</label>
                        <input
                            type="text"
                            id="primeiroNomeProfessor"
                            name="primeiroNomeProfessor"
                            required
                            className="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring-sky-500 focus:border-sky-500 transition duration-150"
                            value={formData.primeiroNomeProfessor}
                            onChange={handleChange}
                        />
                    </div>
                    <div>
                        <label htmlFor="sobrenomeProfessor" className="block text-sm font-medium text-gray-700 mb-1">Sobrenome</label>
                        <input
                            type="text"
                            id="sobrenomeProfessor"
                            name="sobrenomeProfessor"
                            required
                            className="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring-sky-500 focus:border-sky-500 transition duration-150"
                            value={formData.sobrenomeProfessor}
                            onChange={handleChange}
                        />
                    </div>
                </div>
                <div className="mb-4">
                    <label htmlFor="emailProfessor" className="block text-sm font-medium text-gray-700 mb-1">E-mail Institucional</label>
                    <input
                        type="email"
                        id="emailProfessor"
                        name="emailProfessor"
                        required
                        placeholder="seuemail@instituicao.edu.br"
                        className="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring-sky-500 focus:border-sky-500 transition duration-150"
                        value={formData.emailProfessor}
                        onChange={handleChange}
                    />
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-4">
                    <div>
                        <label htmlFor="senhaProfessor" className="block text-sm font-medium text-gray-700 mb-1">Senha</label>
                        <input
                            type="password"
                            id="senhaProfessor"
                            name="senhaProfessor"
                            required
                            className="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring-sky-500 focus:border-sky-500 transition duration-150"
                            value={formData.senhaProfessor}
                            onChange={handleChange}
                        />
                    </div>
                    <div>
                        <label htmlFor="confirmarSenhaProfessor" className="block text-sm font-medium text-gray-700 mb-1">Confirmar Senha</label>
                        <input
                            type="password"
                            id="confirmarSenhaProfessor"
                            name="confirmarSenhaProfessor"
                            required
                            className="w-full p-3 border border-gray-300 rounded-md shadow-sm focus:ring-sky-500 focus:border-sky-500 transition duration-150"
                            value={formData.confirmarSenhaProfessor}
                            onChange={handleChange}
                        />
                    </div>
                </div>
                <div className="mb-6">
                    <label className="flex items-center">
                        <input
                            type="checkbox"
                            id="ehCoordenador"
                            name="ehCoordenador"
                            className="h-5 w-5 text-sky-600 border-gray-300 rounded focus:ring-sky-500"
                            checked={formData.ehCoordenador}
                            onChange={handleChange}
                        />
                        <span className="ml-2 text-sm text-gray-700">Registrar também como Coordenador</span>
                    </label>
                </div>
                {erroSenha && <div id="erroSenhaProfessor" className="text-red-600 text-sm mb-4">As senhas não conferem.</div>}
                <div className="flex justify-end">
                    <button type="submit" className="bg-sky-600 hover:bg-sky-700 text-white font-semibold py-3 px-6 rounded-md shadow-md hover:shadow-lg focus:outline-none focus:ring-2 focus:ring-sky-500 focus:ring-opacity-50 transition duration-150">
                        Cadastrar Professor
                    </button>
                </div>
            </form>
        </section>
    );
};


// Componente principal App
function App() {
    const [activeTab, setActiveTab] = useState('aluno'); // 'aluno' ou 'professor'

    const mostrarFormulario = (tipo) => {
        setActiveTab(tipo);
    };

    return (
        <div className="bg-gray-100 text-gray-800 min-h-screen flex flex-col items-center justify-center py-10 px-4" style={{ fontFamily: "'Inter', sans-serif" }}>
            <div className="bg-white p-8 md:p-12 shadow-xl rounded-lg w-full max-w-2xl">
                <header className="mb-8 text-center">
                    <h1 className="text-3xl md:text-4xl font-bold text-sky-700">Cadastro de Usuários</h1>
                    <p className="text-gray-600 mt-2">Sistema CEPEX</p>
                </header>

                <div className="mb-8 flex border-b border-gray-300">
                    <button
                        id="tabAluno"
                        className={`tab-button flex-1 py-3 px-4 text-center font-semibold border-b-2 border-transparent hover:text-sky-600 hover:border-sky-600 transition-colors duration-150 focus:outline-none ${activeTab === 'aluno' ? 'active text-sky-600 border-sky-600 bg-sky-50' : 'text-gray-600'}`}
                        onClick={() => mostrarFormulario('aluno')}
                    >
                        Cadastrar Aluno
                    </button>
                    <button
                        id="tabProfessor"
                        className={`tab-button flex-1 py-3 px-4 text-center font-semibold border-b-2 border-transparent hover:text-sky-600 hover:border-sky-600 transition-colors duration-150 focus:outline-none ${activeTab === 'professor' ? 'active text-sky-600 border-sky-600 bg-sky-50' : 'text-gray-600'}`}
                        onClick={() => mostrarFormulario('professor')}
                    >
                        Cadastrar Professor
                    </button>
                </div>

                {activeTab === 'aluno' && <AlunoForm />}
                {activeTab === 'professor' && <ProfessorForm />}

            </div>
        </div>
    );
}

export default App;
