using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Media;

namespace AmandaInterface
{
    public partial class About : Form
    {
        string text = "Amanda 2.0\r\n\r\nGemaakt door:\r\n\r\nRemco de Bruin (Bitcoin : 15gSskxrqro61pLRcwxetzigbfR2uXtsD4)\r\nRutger Schoorstra\r\nCallan Kandasamy\r\nMinardus van der Kooi (Bitcoin : 176zAuu4ytyWz7WWfT5R3yWk6KwnGJZZ68)\r\nHieu Duong\r\n\r\nThat's all folks!";
        SoundPlayer simpleSound;
        public About()
        {
            InitializeComponent();
            richTextBox1.Text = text;
            simpleSound = new SoundPlayer(Properties.Resources.about);
            simpleSound.PlayLooping();
        }

        private void richTextBox1_TextChanged(object sender, EventArgs e)
        {

        }

        private void About_FormClosing(object sender, FormClosingEventArgs e)
        {
            simpleSound.Stop();
        }
    }
}
