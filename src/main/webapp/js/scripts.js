
function showDashboard() {
    hideAll();
    document.getElementById('dashboard').style.display = 'block';
    updateDashboard();
    updateMovimentacaoHistorico();
    carregarTiposBebida();
}

function showRegisterForm() {
    hideAll();
    loadTiposBebidaSelect().then(tiposDisponiveis => {
        if (!tiposDisponiveis) {
            Swal.fire('Atenção!', 'Nenhum tipo de bebida disponível. Cadastre ao menos um tipo de bebida antes de registrar uma entrada.', 'warning');
            return;
        }

        loadSecaoSelect().then(secoesDisponiveis => {
            if (!secoesDisponiveis) {
                Swal.fire('Atenção!', 'Nenhuma seção disponível. Cadastre ao menos uma seção antes de registrar uma entrada.', 'warning');
                return;
            }

            document.getElementById('registerForm').style.display = 'block';
        });
    });
}
function loadSecaoSelect() {
    const secaoSelect = document.getElementById('secaoIdEntrada');

    return fetch('/api/secoes')
        .then(response => response.json())
        .then(data => {
            secaoSelect.innerHTML = '';

            if (data.length > 0) {
                data.forEach(secao => {
                    const option = document.createElement('option');
                    option.value = secao.id;
                    option.textContent = `${secao.nome} `;
                    secaoSelect.appendChild(option);
                });
                document.getElementById('secaoError').style.display = 'none';
                return true;
            } else {
                secaoSelect.innerHTML = '<option value="">Nenhuma seção disponível</option>';
                document.getElementById('secaoError').style.display = 'block';
                return false;
            }
        })
        .catch(error => {
            console.error('Erro ao carregar seções:', error);
            Swal.fire('Erro!', 'Ocorreu um erro ao carregar as seções.', 'error');
            return false;
        });
}

function showAllBebidas() {
    hideAll();
    document.getElementById('allBebidas').style.display = 'block';
    loadAllBebidas();
}

function showQuantidadePorSecao() {
    hideAll();
    document.getElementById('quantidadePorSecao').style.display = 'block';
    loadQuantidadePorSecao();
}

function showConfigurarTipos() {
    hideAll();
    document.getElementById('configurarTipos').style.display = 'block';
    loadTiposBebida();
}

function showGerenciarSecoes() {
    hideAll();
    document.getElementById('gerenciarSecoes').style.display = 'block';
    carregarSecoes();
}

function hideAll() {
    document.getElementById('dashboard').style.display = 'none';
    document.getElementById('registerForm').style.display = 'none';
    document.getElementById('quantidadePorSecao').style.display = 'none';
    document.getElementById('allBebidas').style.display = 'none';
    document.getElementById('configurarTipos').style.display = 'none';
    document.getElementById('gerenciarSecoes').style.display = 'none';
}

function loadAllBebidas() {
    const resultadoTodasBebidas = document.getElementById('resultadoTodasBebidas');


    resultadoTodasBebidas.innerHTML = '';

    fetch(`/api/estoque/todas-bebidas`)
        .then(response => response.json())
        .then(data => {
            const tabela = document.createElement('table');
            tabela.className = 'table table-bordered';
            const thead = document.createElement('thead');
            const trHead = document.createElement('tr');
            const thBebida = document.createElement('th');
            thBebida.innerText = 'Bebida';
            trHead.appendChild(thBebida);

            const thSecao = document.createElement('th');
            thSecao.innerText = 'Seção';
            trHead.appendChild(thSecao);

            const thTipoBebida = document.createElement('th');
            thTipoBebida.innerText = 'Tipo de Bebida';
            trHead.appendChild(thTipoBebida);

            const thQuantidade = document.createElement('th');
            thQuantidade.innerText = 'Quantidade (litros)';
            trHead.appendChild(thQuantidade);

            const thAcoes = document.createElement('th');
            thAcoes.innerText = 'Ações';
            trHead.appendChild(thAcoes);

            thead.appendChild(trHead);
            tabela.appendChild(thead);

            const tbody = document.createElement('tbody');

            data.forEach(bebida => {
                const tr = document.createElement('tr');

                const tdBebida = document.createElement('td');
                tdBebida.innerText = bebida.nome;
                tr.appendChild(tdBebida);

                const tdSecao = document.createElement('td');
                tdSecao.innerText = bebida.secao ? bebida.secao.nome : 'Sem seção';
                tr.appendChild(tdSecao);

                const tdTipoBebida = document.createElement('td');
                tdTipoBebida.innerText = bebida.tipoBebida.descricao;
                tr.appendChild(tdTipoBebida);
                const tdQuantidade = document.createElement('td');
                tdQuantidade.innerText = bebida.volume;
                tr.appendChild(tdQuantidade);

                const tdAcoes = document.createElement('td');
                const btnDeletar = document.createElement('button');
                btnDeletar.className = 'btn btn-danger';
                btnDeletar.innerText = 'Saída';
                btnDeletar.onclick = function() {
                    saidaBebida(bebida.id);
                };
                tdAcoes.appendChild(btnDeletar);
                tr.appendChild(tdAcoes);

                tbody.appendChild(tr);
            });

            tabela.appendChild(tbody);
            resultadoTodasBebidas.appendChild(tabela);
            updateDashboard();
        })
        .catch(error => {
            console.error('Erro:', error);
            Swal.fire('Erro!', 'Ocorreu um erro ao consultar todas as bebidas.', 'error');
        });
}

function loadQuantidadePorSecao() {
    const resultadoConsulta = document.getElementById('resultadoConsulta');

    resultadoConsulta.innerHTML = '';

    fetch(`/api/estoque/quantidade-por-secao`)
        .then(response => response.json())
        .then(data => {
            const tabela = document.createElement('table');
            tabela.className = 'table table-bordered';

            const thead = document.createElement('thead');
            const trHead = document.createElement('tr');

            const thTipoBebida = document.createElement('th');
            thTipoBebida.innerText = 'Tipo de bebida';
            trHead.appendChild(thTipoBebida);

            const thVolumeTotal = document.createElement('th');
            thVolumeTotal.innerText = 'Volume total';
            trHead.appendChild(thVolumeTotal);

            thead.appendChild(trHead);
            tabela.appendChild(thead);

            const tbody = document.createElement('tbody');

            const agrupadoPorTipo = data.reduce((acc, item) => {
                const [secao, tipoBebida, volume] = item;
                if (!acc[tipoBebida]) {
                    acc[tipoBebida] = 0;
                }
                acc[tipoBebida] += volume;
                return acc;
            }, {});

            Object.keys(agrupadoPorTipo).forEach(tipoBebida => {
                const tr = document.createElement('tr');

                const tdTipoBebida = document.createElement('td');
                tdTipoBebida.innerText = tipoBebida;
                tr.appendChild(tdTipoBebida);

                const tdVolumeTotal = document.createElement('td');
                tdVolumeTotal.innerText = `${agrupadoPorTipo[tipoBebida]} litros`;
                tr.appendChild(tdVolumeTotal);

                tbody.appendChild(tr);
            });

            tabela.appendChild(tbody);
            resultadoConsulta.appendChild(tabela);
            updateDashboard();
        })
        .catch(error => {
            console.error('Erro:', error);
            Swal.fire('Erro!', 'Ocorreu um erro ao consultar a quantidade por seção.', 'error');
        });
}


function saidaBebida(bebidaId) {
    Swal.fire({
        title: 'Saída Bebida',
        input: 'text',
        inputLabel: 'Nome do Responsável',
        inputPlaceholder: 'Digite o nome do responsável pela saída',
        showCancelButton: true,
        confirmButtonText: 'Saída',
        showLoaderOnConfirm: true,
        preConfirm: (responsavel) => {
            if (!responsavel) {
                Swal.showValidationMessage('Por favor, insira o nome do responsável');
            } else {
                return fetch(`/api/estoque/bebida/${bebidaId}?responsavel=${responsavel}`, {
                    method: 'DELETE',
                })
                    .then(response => {
                        if (response.ok) {
                            Swal.fire('Sucesso!', 'Saída de bebida realizada com sucesso.', 'success');
                            loadAllBebidas();
                            updateDashboard();
                        } else {
                            Swal.fire('Erro!', 'Erro ao dar saída de bebida.', 'error');
                        }
                    })
                    .catch(error => {
                        console.error('Erro:', error);
                        Swal.fire('Erro!', 'Ocorreu um erro ao dar saída de bebida.', 'error');
                    });
            }
        }
    });
}

function updateDashboard() {
    fetch('/api/estoque/volume-total')
        .then(response => response.json())
        .then(data => {
            document.getElementById('totalLitros').innerText = `${data} litros`;
        });

    fetch('/api/estoque/secoes')
        .then(response => response.json())
        .then(data => {
            document.getElementById('totalSecoes').innerText = data.length;
        })
        .catch(error => {
            console.error('Erro ao carregar as seções:', error);
            document.getElementById('totalSecoes').innerText = 'Erro ao carregar os dados';
        });

    fetch('/api/estoque/todas-bebidas')
        .then(response => response.json())
        .then(data => {
            const ul = document.getElementById('nomeBebidas');
            ul.innerHTML = ''; // Limpar a lista antes de adicionar novos itens
            data.forEach(bebida => {
                const li = document.createElement('li');
                li.innerText = bebida.nome;
                ul.appendChild(li);
            });
        })
        .catch(error => {
            console.error('Erro ao carregar as bebidas:', error);
            document.getElementById('nomeBebidas').innerText = 'Erro ao carregar os dados';
        });
}

function updateMovimentacaoHistorico() {
    const movimentacaoHistoricoDiv = document.getElementById('movimentacaoHistorico');

    movimentacaoHistoricoDiv.innerHTML = '';

    fetch('/api/estoque/historico-movimentacoes')
        .then(response => response.json())
        .then(data => {
            const tabela = document.createElement('table');
            tabela.className = 'table table-bordered';
            tabela.id = 'movimentacaoHistoricoTable';

            const thead = document.createElement('thead');
            const trHead = document.createElement('tr');

            const thHorario = document.createElement('th');
            thHorario.innerText = 'Horário';
            trHead.appendChild(thHorario);

            const thTipoMovimentacao = document.createElement('th');
            thTipoMovimentacao.innerText = 'Tipo de Movimentação';
            trHead.appendChild(thTipoMovimentacao);

            const thVolume = document.createElement('th');
            thVolume.innerText = 'Volume';
            trHead.appendChild(thVolume);

            const thSecao = document.createElement('th');
            thSecao.innerText = 'Seção';
            trHead.appendChild(thSecao);

            const thResponsavel = document.createElement('th');
            thResponsavel.innerText = 'Responsável';
            trHead.appendChild(thResponsavel);

            const thTipoBebida = document.createElement('th');
            thTipoBebida.innerText = 'Tipo de Bebida';
            trHead.appendChild(thTipoBebida);

            thead.appendChild(trHead);
            tabela.appendChild(thead);

            const tbody = document.createElement('tbody');

            data.forEach(item => {
                const tr = document.createElement('tr');

                const tdHorario = document.createElement('td');
                tdHorario.innerText = new Date(item[0]).toLocaleString();
                tr.appendChild(tdHorario);

                const tdTipoMovimentacao = document.createElement('td');
                tdTipoMovimentacao.innerText = item[1];
                tr.appendChild(tdTipoMovimentacao);

                const tdVolume = document.createElement('td');
                tdVolume.innerText = item[2];
                tr.appendChild(tdVolume);

                const tdSecao = document.createElement('td');
                tdSecao.innerText = item[3] ? item[3] : 'Não Informada';
                tr.appendChild(tdSecao);

                const tdResponsavel = document.createElement('td');
                tdResponsavel.innerText = item[4] ? item[4] : 'Não Informado';
                tr.appendChild(tdResponsavel);

                const tdTipoBebida = document.createElement('td');
                tdTipoBebida.innerText = item[5] ? item[5] : 'Desconhecido';
                tr.appendChild(tdTipoBebida);

                tbody.appendChild(tr);
            });

            tabela.appendChild(tbody);
            movimentacaoHistoricoDiv.appendChild(tabela);

            $('#movimentacaoHistoricoTable').DataTable({
                order: [[0, 'desc'], [3, 'asc']],
                language: {
                    url: '//cdn.datatables.net/plug-ins/1.11.3/i18n/Portuguese.json'
                },
                initComplete: function () {
                    this.api().columns([1, 3, 5]).every(function () {
                        var column = this;
                        var select = $('<select><option value=""></option></select>')
                            .appendTo($(column.header()))
                            .on('change', function () {
                                var val = $.fn.dataTable.util.escapeRegex(
                                    $(this).val()
                                );

                                column
                                    .search(val ? '^' + val + '$' : '', true, false)
                                    .draw();
                            });

                        column.data().unique().sort().each(function (d, j) {
                            select.append('<option value="' + d + '">' + d + '</option>')
                        });
                    });
                }
            });
        })
        .catch(error => {
            console.error('Erro:', error);
            Swal.fire('Erro!', 'Ocorreu um erro ao carregar as movimentações.', 'error');
        });
}


function loadTiposBebida() {
    const tiposBebidaTableBody = document.getElementById('tiposBebidaTableBody');

    fetch('/api/tipos-bebidas')
        .then(response => response.json())
        .then(data => {
            tiposBebidaTableBody.innerHTML = ''; // Limpa o corpo da tabela

            if (data.length > 0) {
                data.forEach(tipo => {
                    const row = document.createElement('tr');

                    const descricaoCell = document.createElement('td');
                    descricaoCell.textContent = tipo.descricao;searching: false,
                        row.appendChild(descricaoCell);

                    const capacidadeCell = document.createElement('td');
                    capacidadeCell.textContent = tipo.capacidadeMaxima + ' litros';
                    row.appendChild(capacidadeCell);


                    const restricaoCell = document.createElement('td');
                    restricaoCell.textContent = tipo.restricaoQuarentena ? 'Sim' : 'Não';
                    row.appendChild(restricaoCell);

                    const actionsCell = document.createElement('td');
                    const deleteButton = document.createElement('button');
                    deleteButton.className = 'btn btn-danger btn-sm';
                    deleteButton.textContent = 'Deletar';
                    deleteButton.onclick = () => deletarTipoBebida(tipo.id);
                    actionsCell.appendChild(deleteButton);
                    row.appendChild(actionsCell);

                    tiposBebidaTableBody.appendChild(row);
                });
            } else {
                tiposBebidaTableBody.innerHTML = '<tr><td colspan="3">Nenhum tipo de bebida cadastrado</td></tr>';
            }
        })
        .catch(error => {
            console.error('Erro ao carregar tipos de bebida:', error);
        });
}
function loadTiposBebidaSelect() {
    const tipoBebidaSelect = document.getElementById('tipoBebida');

    return fetch('/api/tipos-bebidas')
        .then(response => response.json())
        .then(data => {
            tipoBebidaSelect.innerHTML = '';

            if (data.length > 0) {
                data.forEach(tipo => {
                    const option = document.createElement('option');
                    option.value = tipo.id;
                    option.textContent = tipo.descricao;
                    tipoBebidaSelect.appendChild(option);
                });
                document.getElementById('tipoBebidaError').style.display = 'none';
                return true;
            } else {
                tipoBebidaSelect.innerHTML = '<option value="">Nenhum tipo de bebida disponível</option>';
                document.getElementById('tipoBebidaError').style.display = 'block';
                return false;
            }
        })
        .catch(error => {
            console.error('Erro ao carregar tipos de bebida:', error);
            Swal.fire('Erro!', 'Ocorreu um erro ao carregar os tipos de bebida.', 'error');
            return false;
        });
}


function carregarSecoes() {
    const listaSecoes = document.getElementById('listaSecoes');

    fetch('/api/secoes')
        .then(response => response.json())
        .then(data => {
            listaSecoes.innerHTML = '';

            if (data.length > 0) {
                data.forEach(secao => {
                    const item = document.createElement('li');
                    item.className = 'list-group-item d-flex justify-content-between align-items-center';
                    item.textContent = `Nome seção: ${secao.nome} - Utilização total: ${secao.utilizacaoTotal} litros`;

                    const deleteButton = document.createElement('button');
                    deleteButton.className = 'btn btn-danger btn-sm';
                    deleteButton.textContent = 'Deletar';
                    deleteButton.onclick = () => deletarSecao(secao.id);

                    item.appendChild(deleteButton);
                    listaSecoes.appendChild(item);
                });
            } else {
                listaSecoes.innerHTML = '<li class="list-group-item">Nenhuma seção cadastrada</li>';
            }
        })
        .catch(error => {
            console.error('Erro ao carregar seções:', error);
            Swal.fire('Erro!', 'Ocorreu um erro ao carregar as seções.', 'error');
        });
}

function adicionarSecao() {
    const nomeSecao = document.getElementById("nomeSecao").value;

    fetch("/api/secoes/nova", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            nome: nomeSecao,
            utilizacaoTotal: 0
        })
    })
        .then(response => response.text())
        .then(message => {
            Swal.fire('Sucesso!', message, 'success');
            carregarSecoes();
        })
        .catch(error => console.error("Erro ao adicionar seção:", error));
}

function deletarSecao(secaoId) {
    Swal.fire({
        title: 'Tem certeza?',
        text: "Esta ação não pode ser desfeita!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sim, deletar!',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            fetch(`/api/secoes/${secaoId}`, {
                method: "DELETE"
            })
                .then(response => {
                    if (!response.ok) {
                        return response.text().then(errorMessage => {
                            throw new Error(errorMessage);
                        });
                    }
                    return response.text();
                })
                .then(message => {
                    Swal.fire('Deletado!', message, 'success');
                    carregarSecoes();
                })
                .catch(error => {
                    Swal.fire('Erro!', error.message, 'error');
                });
        }
    });
}

function deletarTipoBebida(tipoBebidaId) {
    Swal.fire({
        title: 'Tem certeza?',
        text: "Esta ação não pode ser desfeita!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sim, deletar!',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            fetch(`/api/tipos-bebidas/${tipoBebidaId}`, {
                method: "DELETE"
            })
                .then(response => response.text())
                .then(message => {
                    Swal.fire('Deletado!', message, 'success');
                    loadTiposBebida(); // Recarregar a lista após deletar
                })
                .catch(error => console.error("Erro ao deletar tipo de bebida:", error));
        }
    });
}

document.getElementById('tipoBebidaForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const descricao = document.getElementById('descricaoTipoBebida').value;
    const capacidadeMaxima = parseFloat(document.getElementById('capacidadeMaximaBebida').value);
    const restricaoQuarentena = document.getElementById('restricaoQuarentena').checked;

    fetch('/api/tipos-bebidas/nova', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ descricao, capacidadeMaxima, restricaoQuarentena })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro na resposta da rede.');
            }
            return response.text();
        })
        .then(data => {
            Swal.fire('Sucesso!', data, 'success');
            document.getElementById('tipoBebidaForm').reset();
            loadTiposBebida(); // Recarrega a lista de tipos de bebidas
        })
        .catch(error => {
            console.error('Erro ao adicionar tipo de bebida:', error);
            Swal.fire('Erro!', 'Ocorreu um erro ao adicionar o tipo de bebida.', 'error');
        });
});
document.getElementById('entradaForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const nome = document.getElementById('nomeBebida').value;
    const volume = parseFloat(document.getElementById('volumeBebida').value);
    const tipoBebida = document.getElementById('tipoBebida').value;
    const secaoId = document.getElementById('secaoIdEntrada').value;
    const responsavel = document.getElementById('responsavelEntrada').value;

    const bebida = {
        nome: nome,
        volume: volume,
        tipoBebida: {
            id: tipoBebida
        }
    };

    fetch(`/api/estoque/entrada?secaoId=${secaoId}&responsavel=${responsavel}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(bebida)
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(errorMessage => {
                    throw new Error(errorMessage);
                });
            }
            return response.text();
        })
        .then(data => {
            Swal.fire('Sucesso!', data, 'success');
            document.getElementById('entradaForm').reset();
            updateDashboard();
        })
        .catch(error => {
            Swal.fire('Erro!', error.message, 'error');  // Exibe a mensagem de erro personalizada
        });
});

document.getElementById("formSecao").addEventListener("submit", function(event) {
    event.preventDefault();
    adicionarSecao();
});

document.addEventListener('DOMContentLoaded', function() {
    loadTiposBebida();
    showGerenciarSecoes();
    carregarSecoes();
    carregarTiposBebida();
    showDashboard();
});

function consultarLocaisDisponiveis() {
    const volumeDesejado = parseFloat(document.getElementById('volumeConsultaDashboard').value);

    if (isNaN(volumeDesejado) || volumeDesejado <= 0) {
        Swal.fire('Erro!', 'Por favor, insira um volume válido.', 'error');
        return;
    }

    fetch(`/api/estoque/locais-disponiveis?volume=${volumeDesejado}`)
        .then(response => response.json())
        .then(data => {
            const resultadoConsulta = document.getElementById('resultadoConsultaLocais');
            resultadoConsulta.innerHTML = '';

            if (data.length === 0) {
                resultadoConsulta.innerText = 'Nenhum local disponível para armazenar o volume solicitado.';
                return;
            }

            const lista = document.createElement('ul');
            lista.className = 'list-group';

            data.forEach(secao => {
                const item = document.createElement('li');
                item.className = 'list-group-item';
                const capacidadeDisponivel = secao.capacidadeDisponivel;
                const tipoBebida = secao.tipoBebidaUtilizada ? ` - Tipo de Bebida: ${secao.tipoBebidaUtilizada}` : '';
                item.innerText = `${secao.nome} - Capacidade Disponível: ${capacidadeDisponivel} litros${tipoBebida}`;
                lista.appendChild(item);
            });

            resultadoConsulta.appendChild(lista);
        })
        .catch(error => {
            console.error('Erro:', error);
            Swal.fire('Erro!', 'Ocorreu um erro ao consultar os locais disponíveis.', 'error');
        });
}

function consultarSecoesDisponiveisParaVenda() {
    const tipoBebidaId = document.getElementById('tipoBebidaVenda').value;

    if (!tipoBebidaId) {
        Swal.fire('Erro!', 'Por favor, selecione um tipo de bebida.', 'error');
        return;
    }

    fetch(`/api/estoque/secoes-disponiveis-venda?tipoBebidaId=${tipoBebidaId}`)
        .then(response => response.json())
        .then(data => {
            const resultadoConsultaVenda = document.getElementById('resultadoConsultaVenda');
            resultadoConsultaVenda.innerHTML = '';

            if (data.length === 0) {
                resultadoConsultaVenda.innerText = 'Nenhuma seção disponível para venda da bebida solicitada.';
                return;
            }

            const lista = document.createElement('ul');
            lista.className = 'list-group';

            data.forEach(secao => {
                const item = document.createElement('li');
                item.className = 'list-group-item';
                item.innerText = `${secao.nome} - Utilização total: ${secao.utilizacaoTotal} litros`;
                lista.appendChild(item);
            });

            resultadoConsultaVenda.appendChild(lista);
        })
        .catch(error => {
            console.error('Erro:', error);
            Swal.fire('Erro!', 'Ocorreu um erro ao consultar as seções disponíveis para venda.', 'error');
        });
}

function carregarTiposBebida() {
    const tipoBebidaSelect = document.getElementById('tipoBebidaVenda');

    fetch('/api/tipos-bebidas')
        .then(response => response.json())
        .then(data => {
            tipoBebidaSelect.innerHTML = '';
            console.log(data);
            if (data.length > 0) {
                data.forEach(tipo => {
                    const option = document.createElement('option');
                    option.value = tipo.id;
                    option.textContent = tipo.descricao;
                    tipoBebidaSelect.appendChild(option);
                });
            } else {
                tipoBebidaSelect.innerHTML = '<option value="">Nenhum tipo de bebida disponível</option>';
            }
        })
        .catch(error => {
            console.error('Erro ao carregar tipos de bebida:', error);
            Swal.fire('Erro!', 'Ocorreu um erro ao carregar os tipos de bebida.', 'error');
        });
}