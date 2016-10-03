package org.recap.service.executor.datadump;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.ReCAPConstants;
import org.recap.model.export.DataDumpRequest;
import org.recap.repository.BibliographicDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by premkb on 27/9/16.
 */
public class FullDataDumpExecutorServiceUT extends BaseTestCase {

    private Logger logger = LoggerFactory.getLogger(FullDataDumpExecutorServiceUT.class);

    @Autowired
    private FullDataDumpExecutorService fullDataDumpExecutorService;

    @Autowired
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Value("${ftp.userName}")
    String ftpUserName;

    @Value("${ftp.knownHost}")
    String ftpKnownHost;

    @Value("${ftp.privateKey}")
    String ftpPrivateKey;

    @Value("${ftp.datadump.remote.server}")
    String ftpDataDumpRemoteServer;

    @Value("${etl.dump.directory}")
    private String dumpDirectoryPath;

    @Value("${datadump.batchsize}")
    private int batchSize;

    @Autowired
    ProducerTemplate producer;

    private String requestingInstitutionCode = "CUL";

    @Test
    public void getFullDumpForMarcXmlFileSystem()throws Exception{
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setNoOfThreads(1);
        dataDumpRequest.setBatchSize(10000);
        dataDumpRequest.setFetchType("0");
        dataDumpRequest.setRequestingInstitutionCode(requestingInstitutionCode);
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        dataDumpRequest.setCollectionGroupIds(cgIds);
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setTransmissionType("2");
        dataDumpRequest.setOutputFormat("0");
        dataDumpRequest.setFileFormat(ReCAPConstants.XML_FILE_FORMAT);
        dataDumpRequest.setDateTimeString(getDateTimeString());
        fullDataDumpExecutorService.process(dataDumpRequest);
        Long totalRecordCount = bibliographicDetailsRepository.countRecordsForFullDump(dataDumpRequest.getCollectionGroupIds(),dataDumpRequest.getInstitutionCodes(),0);
        int loopCount = getLoopCount(totalRecordCount,batchSize);
        Thread.sleep(1000);
        String day = getDateTimeString();
        File file;
        logger.info("file count---->"+loopCount);
        for(int fileCount=1;fileCount<=loopCount;fileCount++){
            file = new File(dumpDirectoryPath+File.separator+ requestingInstitutionCode +File.separator+day+ File.separator  + ReCAPConstants.DATA_DUMP_FILE_NAME+ requestingInstitutionCode +fileCount+"-"+day+ ReCAPConstants.XML_FILE_FORMAT);
            boolean fileExists = file.exists();
            assertTrue(fileExists);
            file.delete();
        }
    }


    @Test
    public void getFullDumpForScsbXmlFileSystem()throws Exception{
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setNoOfThreads(1);
        dataDumpRequest.setBatchSize(10000);
        dataDumpRequest.setFetchType("0");
        dataDumpRequest.setRequestingInstitutionCode(requestingInstitutionCode);
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        dataDumpRequest.setCollectionGroupIds(cgIds);
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("PUL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setTransmissionType("2");
        dataDumpRequest.setOutputFormat("1");
        dataDumpRequest.setFileFormat(ReCAPConstants.XML_FILE_FORMAT);
        dataDumpRequest.setDateTimeString(getDateTimeString());
        fullDataDumpExecutorService.process(dataDumpRequest);
        Long totalRecordCount = bibliographicDetailsRepository.countRecordsForFullDump(dataDumpRequest.getCollectionGroupIds(),dataDumpRequest.getInstitutionCodes(),0);
        int loopCount = getLoopCount(totalRecordCount,batchSize);
        Thread.sleep(1000);
        String day = getDateTimeString();
        File file;
        logger.info("file count---->"+loopCount);
        for(int fileCount=1;fileCount<=loopCount;fileCount++){
            file = new File(dumpDirectoryPath+File.separator+ requestingInstitutionCode +File.separator+day+ File.separator  + ReCAPConstants.DATA_DUMP_FILE_NAME+ requestingInstitutionCode +fileCount+"-"+day+ ReCAPConstants.XML_FILE_FORMAT);
            boolean fileExists = file.exists();
            assertTrue(fileExists);
            //file.delete();
        }
    }

    @Test
    public void getFullDumpForMarcXmlFtp()throws Exception{
        DataDumpRequest dataDumpRequest = new DataDumpRequest();
        dataDumpRequest.setNoOfThreads(1);
        dataDumpRequest.setBatchSize(10000);
        dataDumpRequest.setFetchType("0");
        dataDumpRequest.setRequestingInstitutionCode(requestingInstitutionCode);
        List<Integer> cgIds = new ArrayList<>();
        cgIds.add(1);
        cgIds.add(2);
        dataDumpRequest.setCollectionGroupIds(cgIds);
        List<String> institutionCodes = new ArrayList<>();
        institutionCodes.add("NYPL");
        dataDumpRequest.setInstitutionCodes(institutionCodes);
        dataDumpRequest.setTransmissionType("0");
        dataDumpRequest.setOutputFormat("0");
        dataDumpRequest.setFileFormat(ReCAPConstants.XML_FILE_FORMAT);
        dataDumpRequest.setDateTimeString(getDateTimeString());
        fullDataDumpExecutorService.process(dataDumpRequest);
        Long totalRecordCount = bibliographicDetailsRepository.countRecordsForFullDump(dataDumpRequest.getCollectionGroupIds(),dataDumpRequest.getInstitutionCodes(),0);
        int loopCount = getLoopCount(totalRecordCount,batchSize);
        Thread.sleep(1000);
        String dateTimeString = getDateTimeString();
        logger.info("file count---->"+loopCount);
        String ftpFileName = ReCAPConstants.DATA_DUMP_FILE_NAME+requestingInstitutionCode+"1"+"-"+dateTimeString+ReCAPConstants.XML_FILE_FORMAT;
        ftpDataDumpRemoteServer = ftpDataDumpRemoteServer+ File.separator+requestingInstitutionCode+File.separator+dateTimeString;
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("seda:testFullMarcXmlZipFtp")
                        .pollEnrich("sftp://" +ftpUserName + "@" + ftpDataDumpRemoteServer + "?privateKeyFile="+ ftpPrivateKey + "&knownHostsFile=" + ftpKnownHost + "&fileName="+ftpFileName);
            }
        });
        String response = producer.requestBody("seda:testFullMarcXmlZipFtp", "", String.class);
        Thread.sleep(1000);
        assertNotNull(response);
    }

    private String getDateTimeString(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(ReCAPConstants.DATE_FORMAT_DDMMMYYYYHHMM);
        return sdf.format(date);
    }

    private int getLoopCount(Long totalRecordCount,int batchSize){
        int quotient = Integer.valueOf(Long.toString(totalRecordCount)) / (batchSize);
        int remainder = Integer.valueOf(Long.toString(totalRecordCount)) % (batchSize);
        int loopCount = remainder == 0 ? quotient : quotient + 1;
        return loopCount;
    }
}
