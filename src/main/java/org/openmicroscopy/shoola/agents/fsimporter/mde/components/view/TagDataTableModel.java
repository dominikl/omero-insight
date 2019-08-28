package org.openmicroscopy.shoola.agents.fsimporter.mde.components.view;

import javax.swing.table.DefaultTableModel;

import org.openmicroscopy.shoola.agents.fsimporter.mde.components.ModuleController;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.MDEConfiguration;
import org.openmicroscopy.shoola.agents.fsimporter.mde.configuration.TagNames;
import org.openmicroscopy.shoola.agents.fsimporter.mde.util.TagData;
//import ome.units.unit.Unit;

public class TagDataTableModel extends DefaultTableModel{
	private static String[] idents=new String[] {"Name","Value","Unit","Unit Name","Visible","Default Values","Type","Required"};
	private Class[] columnTypes=new Class[] {String.class,String.class,String.class,String.class,Boolean.class,String.class,String.class,Boolean.class};
	private boolean allEditable;
	private String tableName;
	private MDEConfiguration conf;
	
	public TagDataTableModel(String type,MDEConfiguration conf,boolean allEditable) {
		super(new Object[][] {},idents);
		this.allEditable=allEditable;
		this.conf=conf;
		this.tableName=type;
	}
	
	public void addRow(TagData o)
	{
		super.addRow(parse(o));
	}
	
	//TODO: add jcomponents
	private Object[] parse(TagData c) {
		
		Object[] o=new Object[idents.length];
		if(c!=null) {
			o[0]=c.getTagName();
			o[1]=c.getTagValue();
			o[2]=c.getTagUnitString();
			o[3]=c.getUnitType();
			o[4]=c.isVisible();
			o[5]=c.getDefaultValuesAsString();
			o[6]=c.getTagType();
			o[7]=c.getTagProp();
		}
		return o;
	}
	
	public TagData getRowData(int rowIndex) {
		//test the index
		if ( (rowIndex  >  getRowCount()) || rowIndex  <  0)
			return null;

		String tagname=(String) getValueAt(rowIndex, 0);
		String value=(String) getValueAt(rowIndex, 1);
		String unitSymbol=(String) getValueAt(rowIndex, 2);
		String unitName=(String) getValueAt(rowIndex, 3);
		boolean visible=(boolean) getValueAt(rowIndex, 4);
		String defVal=(String) getValueAt(rowIndex, 5);
		String type=(String) getValueAt(rowIndex, 6);
		boolean req=(boolean) getValueAt(rowIndex, 7);
		
		TagData t=null;
		if(value!=null && value.split(",").length>0) {
			String[] valStrArray=value.split(",");
			//tagdata with unit?
			if(unitSymbol!=null && !unitSymbol.equals("")) {
				ome.model.units.Unit[] u=new ome.model.units.Unit[valStrArray.length];
				for(int i=0; i<valStrArray.length;i++) {
					u[i]=getUnit(valStrArray[i], unitSymbol, unitName,tagname,this.tableName);
				}
				t=new TagData(this.tableName,tagname, u,TagNames.getUnitClass(unitName), req, type);
			}else {
				t=new TagData(this.tableName,tagname,valStrArray,req,type);
			}
		}else {
			if(unitSymbol!=null && !unitSymbol.equals("")) {
				ome.model.units.Unit u=getUnit(value,unitSymbol, unitName,tagname,this.tableName);
				t=new TagData(this.tableName,tagname, u,TagNames.getUnitClass(unitName), req, type);
			}else {
				t=new TagData(this.tableName,tagname,value,req,type);
			}
		}
		
		if(defVal!=null) t.setDefaultValues(defVal.split(","));
		t.setVisible(visible);
		return t;
	}
	
	public void insertRow(int index, TagData o)
	{
		super.insertRow(index, parse(o));
	}
	
	@Override
    public boolean isCellEditable(int row, int column) {
		if(allEditable)
			return true;
		
		if(column==2 || column==4 || column==7)
			return true;
       return false;
    }

	public void addNewRow() {
		addRow(new TagData(this.tableName,"","",false,TagData.TEXTFIELD));
		
	}
	
	
	//TODO: move to class TagNames
	public ome.model.units.Unit getUnit(String val,String unitSymbol,String className,String tagName,String parent){
		// can't create unit obj because no val is given -> put to default unit map
		if(val==null || val.equals("")) {
			conf.addDefaultUnit(unitSymbol,className,tagName,parent);
		}else {
			try {

				double value=Double.parseDouble(val);
				if(className.equals(ome.model.units.ElectricPotential.class.getName()))
					return new ome.model.units.ElectricPotential(value, ome.model.enums.UnitsElectricPotential.bySymbol(unitSymbol));
				if(className.equals(ome.model.units.Power.class.getName()))
					return new ome.model.units.Power(value, ome.model.enums.UnitsPower.bySymbol(unitSymbol));
				if(className.equals(ome.model.units.Frequency.class.getName()))
					return new ome.model.units.Frequency(value, ome.model.enums.UnitsFrequency.bySymbol(unitSymbol));
				if(className.equals(ome.model.units.Pressure.class.getName()))
					return new ome.model.units.Pressure(value, ome.model.enums.UnitsPressure.bySymbol(unitSymbol));
				if(className.equals(ome.model.units.Length.class.getName())) 
					return new ome.model.units.Length(value, ome.model.enums.UnitsLength.bySymbol(unitSymbol));
				if(className.equals(ome.model.units.Temperature.class.getName()))
					return new ome.model.units.Temperature(value, ome.model.enums.UnitsTemperature.bySymbol(unitSymbol));
				if(className.equals(ome.model.units.Time.class.getName()))
					return new ome.model.units.Time(value, ome.model.enums.UnitsTime.bySymbol(unitSymbol));
			}catch(Exception e) {
				System.out.println("--ERROR: can't parse unit value: "+val+"[TagDataTableModel::getUnit]");
				e.printStackTrace();
			}
		}
		return null;
			
	}
}
