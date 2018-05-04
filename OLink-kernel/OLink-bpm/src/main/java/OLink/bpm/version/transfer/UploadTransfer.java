package OLink.bpm.version.transfer;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.upload.ejb.UploadProcess;
import OLink.bpm.core.upload.ejb.UploadVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcessBean;
import OLink.bpm.core.dynaform.form.ejb.FormField;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.log4j.Logger;

public class UploadTransfer extends BaseTransfer
{
  private static final Logger LOG = Logger.getLogger(UploadTransfer.class);

  public void to2_4()
  {
    
  }

  public void to2_5()
  {
    Connection conn = getConnection();
    try {
      QueryRunner qRunner = new QueryRunner();
      FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);

      ApplicationProcess applicationProcess = (ApplicationProcess)ProcessFactory.createProcess(ApplicationProcess.class);

      String sql = "SELECT id, name  FROM T_DYNAFORM_FORM WHERE type !=256 and (templatecontext like '%ImageUploadField%' or templatecontext like '%AttachmentUploadField%')";
      List dataList = (List)qRunner.query(conn, sql, new MapListHandler());
      boolean transfered = true;
      LOG.info("---->begin transfer datasource data to t_upload in tlk_...");
      Iterator iterator = dataList.iterator(); while (iterator.hasNext()) {
        Map data = (Map)iterator.next();

        Form form = (Form)formProcess.doView((String)data.get("id"));
        ApplicationVO applicationVO = (ApplicationVO)applicationProcess.doView(form.getApplicationid());
        UploadProcess uploadProcess = (UploadProcess)ProcessFactory.createRuntimeProcess(UploadProcess.class, form.getApplicationid());
        Iterator iteratorForm = form.getValueStoreFields().iterator(); while (iteratorForm.hasNext())
        {
          FormField formField = (FormField)iteratorForm.next();

          if ((formField.getType() != null) && ((
            (formField.getType().equals("imageupload")) || (formField.getType().equals("attachmentupload"))))) {
            DocumentProcess documentPross = new DocumentProcessBean(form.getApplicationid());
            if (applicationVO.getDomains() != null) {
              Iterator iteratorDomainVO = applicationVO.getDomains().iterator(); while (iteratorDomainVO.hasNext()) {
                DataPackage dataPackage = documentPross.queryByDQL("$formname=" + form.getFullName(), ((DomainVO)iteratorDomainVO.next()).getId());
                if (dataPackage.rowCount > 0) {
                  Iterator iteratorDocument = dataPackage.datas.iterator(); while (iteratorDocument.hasNext()) {
                    Document documentVO = (Document)iteratorDocument.next();
                    String value = documentVO.getValueByField(formField.getName());
                    if ((value != null) && (!(value.equals("")))) {
                      String[] values = value.split(";");
                      if ((values != null) && (values.length > 0))
                        for (int i = 0; i < values.length; ++i)
                          if (values[i].lastIndexOf("_") != -1)
                          {
                            String webPath = values[i].substring(0, values[i].lastIndexOf("_"));

                            String realPath = System.getenv("PROJECT_HOME") + webPath.replaceAll("/", "\\\\");

                            String name = values[i].substring(values[i].lastIndexOf("_") + 1);
                            String extName = name.substring(name.lastIndexOf("."));

                            String reWebPath = "";
                            String reRealPath = "";

                            String realDir = realPath.substring(0, realPath.lastIndexOf("\\") + 1);

                            File file = new File(realPath);

                            StringBuffer sb = new StringBuffer();
                            reName(sb, realDir, 0, name.substring(0, name.lastIndexOf(".")), extName);
                            reWebPath = webPath.substring(0, webPath.lastIndexOf("/") + 1) + sb.toString();
                            reRealPath = realDir + sb.toString();
                            System.out.println("-->reName:" + sb.toString());

                            if (file.exists()) {
                              try {
                                File file1 = new File(realDir + sb.toString());
                                try {
                                  file.renameTo(file1);
                                } catch (Exception e) {
                                  LOG.info("---->文件重命名错误！" + realPath + " --- " + reRealPath);
                                  e.printStackTrace();
                                }
                              } catch (Exception e) {
                                LOG.info("---->文件重命名错误！" + realPath + " --- " + reRealPath);
                                e.printStackTrace();
                              }

                            }

                            documentVO.findItem(formField.getName()).setValue(reWebPath);
                            documentPross.doUpdate(documentVO);

                            UploadVO uploadVO = uploadProcess.findByColumnName1("PATH", reWebPath);
                            if (uploadVO == null) {
                              transfered = false;
                              uploadVO = new UploadVO();
                              uploadVO.setId(UUID.randomUUID().toString());
                              uploadVO.setName(values[i].substring(values[i].lastIndexOf("_") + 1));
                              uploadVO.setFieldid(documentVO.getId());

                              if (file.exists())
                                uploadVO.setSize(file.length());

                              uploadVO.setModifyDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

                              uploadVO.setType(extName);
                              uploadVO.setPath(reWebPath);
                              uploadVO.setFolderPath(reWebPath.substring(0, reWebPath.lastIndexOf("/")));
                              uploadProcess.doCreate(uploadVO);
                            } else {
                              uploadVO.setName(values[i].substring(values[i].lastIndexOf("_") + 1));
                              uploadVO.setFieldid(documentVO.getId());

                              if (file.exists())
                                uploadVO.setSize(file.length());

                              uploadVO.setModifyDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

                              uploadVO.setType(extName);
                              uploadVO.setPath(reWebPath);
                              uploadVO.setFolderPath(reWebPath.substring(0, reWebPath.lastIndexOf("/")));
                              uploadProcess.doUpdate(uploadVO);
                            }
                          }
                    }
                  }
                }
              }
            }
          }

        }

      }

      if (transfered) {
        LOG.info("---->no data to transfer!"); return;
      }
      LOG.info("---->transfer data successfuly!");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      LOG.info("---->" + e.getMessage());
    } catch (SQLException e) {
      e.printStackTrace();
      LOG.info("---->" + e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      LOG.info("---->" + e.getMessage());
    }
  }

  protected void reName(StringBuffer sb, String realPath, int i, String fileName, String extName)
  {
    File file = new File(realPath + fileName + extName);
    if (file.exists()) {
      ++i;
      reName(sb, realPath, i, fileName + i, extName);
    } else {
      sb.append(fileName + extName);
    }
  }

  public static void main(String[] args)
  {
    new UploadTransfer().to2_5();
  }
}