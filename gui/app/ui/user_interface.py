import os
import tkinter as tk
from tkinter import filedialog

from app.service.beer_service import BeerService

WINDOW_TITLE = "De biermachine"
SELECT_IMAGE_TEXT = "Selecteer een foto"


class UserInterface:

    def __init__(self):
        self.beer_service = BeerService(self.__finished_brewing_action)
        self.beer_service.start_receiving()

        self.window = tk.Tk()
        self.window.title(WINDOW_TITLE)
        frame = tk.Frame(self.window, width=300, height=200)
        frame.pack(expand=True, fill=tk.BOTH)
        self.button = tk.Button(frame, text=SELECT_IMAGE_TEXT, command=self.__upload_action, height=2, width=50)
        self.text_area = tk.Text(frame, height=2, width=50)
        self.button.pack(expand=True, fill=tk.BOTH)

    def show(self) -> None:
        self.window.mainloop()

    def __upload_action(self, event=None) -> None:
        home = os.path.expanduser('~')
        filename = filedialog.askopenfilename(initialdir=home, title=SELECT_IMAGE_TEXT, filetypes=(("jpg", "*.jpg"), ("jpeg", "*.jpeg"), ("png", "*.png")))
        if filename:
            self.beer_service.upload_image(filename)
            self.button.pack_forget()
            self.text_area.pack(expand=True, fill=tk.BOTH)
            self.__set_text("Brewing...")

    def __finished_brewing_action(self, beer_name: str) -> None:
        self.__set_text("Beer served: {}".format(beer_name))
        self.button.pack(expand=True, fill=tk.BOTH)

    def __set_text(self, text: str) -> None:
        self.text_area.delete("1.0", tk.END)
        self.text_area.insert(tk.END, text)
