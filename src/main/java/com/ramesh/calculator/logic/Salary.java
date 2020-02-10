package com.ramesh.calculator.logic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import model.BonusSheetVO;
import model.Criteria;
import model.CrownBonus;
import model.SalarySheetVO;

public class Salary {
	private int skId;
	private int crownLevel;
	private boolean isFirstMonth;
	private int liveDuratiion;
	private int validDays;
	private int actualEarning;
	private int bonusGems;
	private int salary;
	private List<BonusSheetVO> bonusSheet = new ArrayList<BonusSheetVO>();
	private List<SalarySheetVO> salarySheet = new ArrayList<SalarySheetVO>();
	private Criteria criteria;
	private CrownBonus crownBonus;

	int calculateSalary() {
		salary += actualEarning / 20000;
		// if()
		return 0;
	}

	void populateData() throws EncryptedDocumentException, IOException {
		String file = getClass().getClassLoader().getResource("Earning.xls").getFile();
		Workbook wb = WorkbookFactory.create(new File(file));
		Sheet criteriaSheet = wb.getSheet("Criteria");
		Criteria criteria = new Criteria();
		for (Row row : criteriaSheet) {
			if (row.getRowNum() == 0) {
				continue;
			}
			for (Cell cell : row) {
				if (cell.getColumnIndex() == 0) {
					criteria.setDuration(new Double(cell.getNumericCellValue()).intValue());
					System.out.println("Criteria: " + criteria.getDuration());
				}
//				if (counter ==1)
//					criteria.setBonusDuration(cell.getnum);
//				if(counter )
			}
		}
	}

	public static void main(String[] args) throws EncryptedDocumentException, IOException {
		Salary s = new Salary();
		s.populateData();
	}
}
