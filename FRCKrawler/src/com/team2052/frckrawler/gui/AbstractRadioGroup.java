package com.team2052.frckrawler.gui;

import java.util.ArrayList;

import android.util.Log;
import android.widget.RadioButton;

public class AbstractRadioGroup {
	
	private int selectedButtonPos;
	private ArrayList<RadioButton> buttons;
	
	public AbstractRadioGroup() {
		selectedButtonPos = -1;
		buttons = new ArrayList<RadioButton>();
	}
	
	public AbstractRadioGroup(ArrayList<RadioButton> _buttons) {
		selectedButtonPos = -1;
		buttons = _buttons;
	}
	
	public void add(RadioButton button) {
		buttons.add(button);
	}
	
	public void remove(RadioButton button) {
		buttons.remove(button);
	}
	
	public RadioButton getSelectedButton() {
		if(selectedButtonPos != -1)
			return buttons.get(selectedButtonPos);
		else
			return null;
	}
	
	/*****
	 * Method: notifyClick
	 * 
	 * @param clickedButton
	 * 
	 * Summary: Must be for the group effect to take place on a click. Must be used 
	 * with an OnClickListener! Any other type of listener will produce unpredictable
	 * results.
	 *****/
	
	public void notifyClick(RadioButton clickedButton) {
		for(int i = 0; i < buttons.size(); i++) {
			if(buttons.get(i) != null && buttons.get(i) != clickedButton)
				buttons.get(i).setChecked(false);
			
			else if(buttons.get(i) == clickedButton) {
				selectedButtonPos = i;
			}
		}
	}
	
	public void selectButton(RadioButton button) {
		button.setChecked(true);
		notifyClick(button);
	}
}
