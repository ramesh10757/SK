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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ramesh.calculator.model.BonusSheetVO;
import com.ramesh.calculator.model.Criteria;
import com.ramesh.calculator.model.CrownBonus;
import com.ramesh.calculator.model.SalarySheetVO;

@RestController
public class Salary {
	private int skId;
	private int crownLevel;
	private boolean isFirstMonth;
	private int liveDuration;
	private int validDays;
	private int actualEarning;
	private int bonusGems;
	private int salary;
	private List<BonusSheetVO> bonusSheet = new ArrayList<BonusSheetVO>();
	private List<SalarySheetVO> salarySheet = new ArrayList<SalarySheetVO>();
	private Criteria criteria;
	private CrownBonus crownBonus;
	
	@RequestMapping("/")
	String home() {
		return "Welcome to Streamkar Shalini Agency";
	}
	
	@RequestMapping("/calculate")
	public String controllerMain(@RequestParam String skId, @RequestParam String crownLevel, @RequestParam boolean isFirstMonth, @RequestParam String liveDuration,
			@RequestParam String validDays, @RequestParam String actualEarning, @RequestParam String bonusGems) throws EncryptedDocumentException, IOException {
		Salary s = new Salary();
		s.populateData();
		s.initSalary(Integer.parseInt(skId), Integer.parseInt(crownLevel), isFirstMonth, Integer.parseInt(liveDuration),
				Integer.parseInt(validDays), Integer.parseInt(actualEarning), Integer.parseInt(bonusGems));
		// s.initSalary(123, 3, false, 70, 15, 10000000, 300000);
		int finalSalary = s.calculateSalary();
		System.out.println("My final salary: " + finalSalary * 65);
		return "\u20B9 " +  String.valueOf(finalSalary*65);
	}

	public void initSalary(int skId, int crownLevel, boolean isFirstMonth, int liveDuration, int validDays,
			int actualEarning, int bonusGems) {
		this.skId = skId;
		this.crownLevel = crownLevel;
		this.isFirstMonth = isFirstMonth;
		this.liveDuration = liveDuration;
		this.validDays = validDays;
		this.actualEarning = actualEarning;
		this.bonusGems = bonusGems;
	}

	int calculateSalary() {
		if (actualEarning >= 200000) {
			salary += (actualEarning + bonusGems) / 20000;
			System.out.println("Gems Sharing: " + salary);
			// Calculate Basic Salary
			if (liveDuration >= criteria.getDuration() && validDays >= criteria.getValidDays()) {
				salary += getBasicSalary(actualEarning);
			}
			// calculate bonus if applicable
			if (liveDuration >= criteria.getBonusDuration() && validDays >= criteria.getValidDays()) {
				salary += getBonus(actualEarning);
			}
			// calculate crown bonus if applicable
			if (crownLevel >= crownBonus.getExpectedCrownLevel() && liveDuration >= crownBonus.getRequriedDuration()
					&& actualEarning >= crownBonus.getRequiredEarning() && validDays >= criteria.getValidDays()) {
				salary += crownBonus.getBonusAmount();
			}
			// new joinee bonus
			if (isFirstMonth && actualEarning >= 1000000 && liveDuration >= criteria.getDuration()
					&& validDays >= criteria.getValidDays()) {
				salary += 50;
			}
			return salary;
		}
		return 0;
	}

	private int getBonus(int actualEarning) {
		for (BonusSheetVO bonusRow : bonusSheet) {
			if (actualEarning >= bonusRow.getMinEarning() && actualEarning <= bonusRow.getMaxEarning()) {
				System.out.println("Found Bonus: " + bonusRow.getBonusAmount());
				return bonusRow.getBonusAmount();
			}
		}
		return 0;
	}

	private int getBasicSalary(int actualEarning) {
		for (SalarySheetVO salaryRow : salarySheet) {
			if (actualEarning < salaryRow.getGemsRequirement()) {
				continue;
			} else {
				System.out.println("Basic Salary calculated: " + salaryRow.getBasicSalary());
				;
				return salaryRow.getBasicSalary();
			}
		}
		return 0;
	}

	void populateData() throws EncryptedDocumentException, IOException {
		String file = getClass().getClassLoader().getResource("Earning.xls").getFile();
		Workbook wb = WorkbookFactory.create(new File(file));
		populateCriteria(wb);
		populateCrownBonus(wb);
		populateSalaryChart(wb);
		populateBonusSheet(wb);
	}

	private void populateBonusSheet(Workbook wb) {
		Sheet bonus = wb.getSheet("Bonus");
		for (Row row : bonus) {
			if (row.getRowNum() == 0) {
				continue;
			}
			BonusSheetVO currRow = new BonusSheetVO();
			for (Cell cell : row) {
				if (cell.getColumnIndex() == 0) {
					currRow.setMinEarning(new Double(cell.getNumericCellValue()).intValue());
					System.out.print("Min earning: " + currRow.getMinEarning());
				}
				if (cell.getColumnIndex() == 1) {
					currRow.setMaxEarning(new Double(cell.getNumericCellValue()).intValue());
					System.out.print("Max earning: " + currRow.getMaxEarning());
				}
				if (cell.getColumnIndex() == 2) {
					currRow.setBonusAmount(new Double(cell.getNumericCellValue()).intValue());
					System.out.println(" Basic Salary: " + currRow.getBonusAmount());
				}
			}
			bonusSheet.add(currRow);
		}
	}

	private void populateSalaryChart(Workbook wb) {
		Sheet salaryChart = wb.getSheet("SalaryChart");
		for (Row row : salaryChart) {
			if (row.getRowNum() == 0) {
				continue;
			}
			SalarySheetVO currRow = new SalarySheetVO();
			for (Cell cell : row) {
				if (cell.getColumnIndex() == 0) {
					currRow.setGemsRequirement(new Double(cell.getNumericCellValue()).intValue());
					System.out.print("Gems target: " + currRow.getGemsRequirement());
				}
				if (cell.getColumnIndex() == 1) {
					currRow.setGemsSharing(new Double(cell.getNumericCellValue()).intValue());
					System.out.print(" Gems Sharing: " + currRow.getGemsSharing());
				}
				if (cell.getColumnIndex() == 2) {
					currRow.setBasicSalary(new Double(cell.getNumericCellValue()).intValue());
					System.out.println(" Basic Salary: " + currRow.getBasicSalary());
				}
			}
			salarySheet.add(currRow);
		}
	}

	private void populateCrownBonus(Workbook wb) {
		Sheet crownBonusSheet = wb.getSheet("CrownBonus");
		crownBonus = new CrownBonus();
		for (Row row : crownBonusSheet) {
			if (row.getRowNum() == 0) {
				continue;
			}
			for (Cell cell : row) {
				if (cell.getColumnIndex() == 0) {
					crownBonus.setRequiredEarning(((new Double(cell.getNumericCellValue()).intValue())));
					System.out.println("Required Earning for crown bonus: " + crownBonus.getRequiredEarning());
				}
				if (cell.getColumnIndex() == 1) {
					crownBonus.setRequriedDuration(((new Double(cell.getNumericCellValue()).intValue())));
					System.out.println("Required Duration for crown: " + crownBonus.getRequriedDuration());
				}
				if (cell.getColumnIndex() == 2) {
					crownBonus.setBonusAmount((new Double(cell.getNumericCellValue()).intValue()));
					System.out.println("crownBonus: " + crownBonus.getBonusAmount());
				}
				if (cell.getColumnIndex() == 3) {
					crownBonus.setExpectedCrownLevel((new Double(cell.getNumericCellValue()).intValue()));
					System.out.println("expected crown level: " + crownBonus.getExpectedCrownLevel());
				}
			}
		}
	}

	private void populateCriteria(Workbook wb) {
		Sheet criteriaSheet = wb.getSheet("Criteria");
		criteria = new Criteria();
		for (Row row : criteriaSheet) {
			if (row.getRowNum() == 0) {
				continue;
			}
			for (Cell cell : row) {
				if (cell.getColumnIndex() == 0) {
					criteria.setDuration(new Double(cell.getNumericCellValue()).intValue());
					System.out.println("Criteria: " + criteria.getDuration());
				}
				if (cell.getColumnIndex() == 1) {
					criteria.setValidDays((new Double(cell.getNumericCellValue()).intValue()));
					System.out.println("Valid Days: " + criteria.getValidDays());
				}
				if (cell.getColumnIndex() == 2) {
					criteria.setBonusDuration((new Double(cell.getNumericCellValue()).intValue()));
					System.out.println("Bonus Duration: " + criteria.getBonusDuration());
				}
			}
		}
	}

	
}
