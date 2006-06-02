package org.alfresco.web.bean.actions.handlers;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.TransformActionExecuter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.actions.BaseActionWizard;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.bean.wizard.IWizardBean;

/**
 * Action handler for the "transform" action.
 * 
 * @author gavinc
 */
public class TransformHandler extends BaseActionHandler
{
   protected static final String PROP_TRANSFORMER = "transformer";
   
   public String getJSPPath()
   {
      return getJSPPath(TransformActionExecuter.NAME);
   }

   public void prepareForSave(Map<String, Serializable> actionProps,
         Map<String, Serializable> repoProps)
   {
      // add the transformer to use
      repoProps.put(TransformActionExecuter.PARAM_MIME_TYPE,
            actionProps.get(PROP_TRANSFORMER));
      
      // add the destination space id to the action properties
      NodeRef destNodeRef = (NodeRef)actionProps.get(PROP_DESTINATION);
      repoProps.put(TransformActionExecuter.PARAM_DESTINATION_FOLDER, destNodeRef);
      
      // add the type and name of the association to create when the copy
      // is performed
      repoProps.put(TransformActionExecuter.PARAM_ASSOC_TYPE_QNAME, 
            ContentModel.ASSOC_CONTAINS);
      repoProps.put(TransformActionExecuter.PARAM_ASSOC_QNAME, 
            QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "copy"));
   }

   public void prepareForEdit(Map<String, Serializable> actionProps,
         Map<String, Serializable> repoProps)
   {
      String transformer = (String)repoProps.get(TransformActionExecuter.PARAM_MIME_TYPE);
      actionProps.put(PROP_TRANSFORMER, transformer);
      
      NodeRef destNodeRef = (NodeRef)repoProps.get(TransformActionExecuter.PARAM_DESTINATION_FOLDER);
      actionProps.put(PROP_DESTINATION, destNodeRef);
   }

   public String generateSummary(FacesContext context, IWizardBean wizard,
         Map<String, Serializable> actionProps)
   {
      String label = null;
      NodeRef space = (NodeRef)actionProps.get(PROP_DESTINATION);
      String name = Repository.getNameForNode(
            Repository.getServiceRegistry(context).getNodeService(), space);
      String transformer = (String)actionProps.get(PROP_TRANSFORMER);
      
      // find the label used by looking through the SelectItem list
      for (SelectItem item : ((BaseActionWizard)wizard).getTransformers())
      {
         if (item.getValue().equals(transformer))
         {
            label = item.getLabel();
            break;
         }
      }
      
      return MessageFormat.format(Application.getMessage(context, "action_transform"),
            new Object[] {name, label});
   }
}
