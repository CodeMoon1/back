package com.oceanodosdados.reportGenerator;

import com.oceanodosdados.records.HubDoDevResponse;
import java.io.IOException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.Set;


@Component
public class ReportType1 implements IReport {


    private XSSFWorkbook workbook;
    private XSSFSheet sheetRoots;
    private XSSFSheet sheetTelefones;
    private XSSFSheet sheetEnderecos;
    private XSSFSheet sheetContatos;
    private FileOutputStream out;

    private String[] titulos3 = {"CPF", "Email"};
    private String[] titulos1 = {"CPF", "Logradouro", "Numero", "Complemento", "Bairro", "Cidade", "UF","CEP"};
    private String[] titulos2 = {"CPF","Telefone Com DDD", "Telemarketing Bloqueado", "Operadora", "Tipo Telefone", "Tipo Telefone","WhatsApp"};
    private String[] titulos0 = {"CPF", "Nome Completo", "Gênero", "Data de Nascimento", "Documento", "Nome da Mãe", "Idade","Ultima Atualização"};

    public ReportType1(){
        workbook = new XSSFWorkbook();
        sheetRoots = workbook.createSheet("Pessoas");
        sheetTelefones = workbook.createSheet("Telefones");
        sheetEnderecos = workbook.createSheet("Enderecos");
        sheetContatos = workbook.createSheet("Contatos");
    }
    //Metodo para gerar o arquivo excel retornando um array de bytes
    @Override
    public byte[] generate(Set<HubDoDevResponse> roots) throws IOException {
        try (
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            geradorCabecalho(sheetRoots, titulos0);
            geradorCabecalho(sheetTelefones, titulos2);
            geradorCabecalho(sheetEnderecos, titulos1);
            geradorCabecalho(sheetContatos, titulos3);
            pupulandoArquivo(roots);
            workbook.write(outputStream);
            //da algum jeto de fechar o workbook aqui
            //workbook.close();
            return outputStream.toByteArray();
        }
    }

    private void geradorCabecalho(XSSFSheet sheet, String[] titulos) {
        workbook = sheet.getWorkbook();
        Row cabecalho = sheet.createRow(0);
        CellStyle estiloCabecalho = workbook.createCellStyle();
        Font fonteCabecalho = workbook.createFont();
        fonteCabecalho.setBold(true);
        fonteCabecalho.setFontHeightInPoints((short) 12);
        estiloCabecalho.setFont(fonteCabecalho);
        estiloCabecalho.setAlignment(HorizontalAlignment.CENTER);
        estiloCabecalho.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloCabecalho.setBorderTop(BorderStyle.THIN);
        estiloCabecalho.setBorderBottom(BorderStyle.THIN);
        estiloCabecalho.setBorderLeft(BorderStyle.THIN);
        estiloCabecalho.setBorderRight(BorderStyle.THIN);

        for(int i = 0;i < titulos.length;i++){
            Cell cell = cabecalho.createCell(i);
            cell.setCellValue(titulos[i]);
            cell.setCellStyle(estiloCabecalho);
            int larguraPadrao = 8000;
            sheet.setColumnWidth(i, larguraPadrao);
        }
    }


    private void pupulandoArquivo(Set<HubDoDevResponse> roots) {
        int rownum = 1;
        int rownum2 = 1;
        int rownum3 = 1;
        int rownum4 = 1;

        for(HubDoDevResponse i : roots){
            Row row = sheetRoots.createRow(rownum++);
            int cellnum = 0;

            Cell cellCpf = row.createCell(cellnum++);
            cellCpf.setCellValue(i.result().codigoPessoa());

            Cell cellNome = row.createCell(cellnum++);
            cellNome.setCellValue(i.result().nomeCompleto());

            Cell cellGenero = row.createCell(cellnum++);
            cellGenero.setCellValue(i.result().genero());

            Cell cellDtNasc = row.createCell(cellnum++);
            cellDtNasc.setCellValue(i.result().dataDeNascimento());

            Cell cellDoc = row.createCell(cellnum++);
            cellDoc.setCellValue(i.result().documento());

            Cell cellNomeMae = row.createCell(cellnum++);
            cellNomeMae.setCellValue(i.result().nomeCompleto());

            Cell celIdade = row.createCell(cellnum++);
            celIdade.setCellValue(i.result().anos());

            Cell cellLastUpdate = row.createCell(cellnum++);
            cellLastUpdate.setCellValue(i.result().lastUpdate());


            for(HubDoDevResponse.Result.Telefone j : i.result().listaTelefones()){
                int cellnum2 = 0;
                Row row2 = sheetTelefones.createRow(rownum2++);

                Cell cellCpfSubListaTel = row2.createCell(cellnum2++);
                cellCpfSubListaTel.setCellValue(i.result().codigoPessoa());

                Cell cellTell = row2.createCell(cellnum2++);
                cellTell.setCellValue(j.telefoneComDDD());

                Cell cellTelema = row2.createCell(cellnum2++);
                cellTelema.setCellValue(j.telemarketingBloqueado());

                Cell cellOper = row2.createCell(cellnum2++);
                cellOper.setCellValue(j.operadora());

                Cell cellTipo = row2.createCell(cellnum2++);
                cellTipo.setCellValue(j.tipoTelefone());

                Cell cellWhats = row2.createCell(cellnum2++);
                cellWhats.setCellValue(j.whatsApp());
            }

            for(HubDoDevResponse.Result.Endereco z : i.result().listaEnderecos()){
                int cellnum3 = 0;
                Row row3 = sheetEnderecos.createRow(rownum3++);

                Cell cellCpfSubListaEnd = row3.createCell(cellnum3++);
                cellCpfSubListaEnd.setCellValue(i.result().codigoPessoa());

                Cell cellLagra = row3.createCell(cellnum3++);
                cellLagra.setCellValue(z.logradouro());

                Cell cellNum = row3.createCell(cellnum3++);
                cellNum.setCellValue(z.numero());

                Cell cellComple = row3.createCell(cellnum3++);
                cellComple.setCellValue(z.complemento());

                Cell cellBairro = row3.createCell(cellnum3++);
                cellBairro.setCellValue(z.bairro());

                Cell cellCidade = row3.createCell(cellnum3++);
                cellCidade.setCellValue(z.cidade());

                Cell cellUf = row3.createCell(cellnum3++);
                cellUf.setCellValue(z.uf());

                Cell cellCep = row3.createCell(cellnum3++);
                cellCep.setCellValue(z.cep());
            }

            for(HubDoDevResponse.Result.Email k : i.result().listaEmails()){
                int cellnum4 = 0;
                Row row4 = sheetContatos.createRow(rownum4++);

                Cell cellCpfSubListaEmail = row4.createCell(cellnum4++);
                cellCpfSubListaEmail.setCellValue(i.result().codigoPessoa());

                Cell cellEmail = row4.createCell(cellnum4++);
                cellEmail.setCellValue(k.enderecoEmail());

            }
        }
    }

}